package com.example.demo.controllers;

import com.auth0.jwt.JWT;
import com.example.demo.model.handle.ServiceException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.response.LoginResponse;
import com.example.demo.security.SecurityConstants;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	AuthenticationManager authManager;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("view detail user id {}",id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("find by username {}",username);
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws ServiceException {
		try {
			logger.info("create user {}",createUserRequest.getUsername());
			User user = new User(createUserRequest.getUsername(), bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			logger.debug("Here is some DEBUG");
			logger.info("Here is some INFO");
			logger.warn("Here is some WARN");
			logger.error("Here is some ERROR");
			return ResponseEntity.ok(user);
		}
		catch (Exception e){
			logger.error("error create user");
			throw new ServiceException("error create user");

		}

	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody CreateUserRequest request) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(), request.getPassword())
			);
			String token = JWT.create()
					.withSubject(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
					.sign(HMAC512(SecurityConstants.SECRET.getBytes()));

			LoginResponse LoginResponse = new LoginResponse();
			LoginResponse.setSuccess(true);
			LoginResponse.setToken(token);

			return ResponseEntity.ok(LoginResponse);
		}catch (Exception e){
			return ResponseEntity.badRequest().body(new LoginResponse());
		}

	}
	
}
