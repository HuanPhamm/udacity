package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {


	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private CartController cartController;

	@InjectMocks
	private OrderController orderController;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CartRepository cartRepository;

	@BeforeEach
	public void setup() {
		initMocks(this);
	}

	@Test
	public void testAddToCart() {
		User user = new User();
		user.setUsername("testUser");
		user.setPassword("testPassword");
		Cart cart = new Cart();
		cart.setUser(user);
		user.setCart(cart);
		Item item = new Item();
		item.setId(1L);
		item.setName("Test Item");
		item.setPrice(BigDecimal.valueOf(10.99));
		ModifyCartRequest request = new ModifyCartRequest();
		request.setUsername("testUser");
		request.setItemId(1L);
		request.setQuantity(2);
		when(userRepository.findByUsername("testUser")).thenReturn(user);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Cart responseCart = responseEntity.getBody();
		assertEquals(2, responseCart.getItems().size());
		assertEquals(BigDecimal.valueOf(21.98), responseCart.getTotal());
	}

	@Test
	public void testRemoveFromCart() {
		User user = new User();
		user.setUsername("testUser");
		user.setPassword("testPassword");
		Cart cart = new Cart();
		cart.setUser(user);
		user.setCart(cart);
		Item item = new Item();
		item.setId(1L);
		item.setName("Test Item");
		item.setPrice(BigDecimal.valueOf(10.99));
		cart.addItem(item);
		cart.addItem(item);
		ModifyCartRequest request = new ModifyCartRequest();
		request.setUsername("testUser");
		request.setItemId(1L);
		request.setQuantity(1);
		when(userRepository.findByUsername("testUser")).thenReturn(user);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Cart responseCart = responseEntity.getBody();
		assertEquals(1, responseCart.getItems().size());
		assertEquals(BigDecimal.valueOf(10.99), responseCart.getTotal());

	}



	@Test
	public void testGetOrdersForUser() {
		User user = new User();
		user.setUsername("testUser");
		user.setPassword("testPassword");
		List<UserOrder> orders = new ArrayList<>();
		orders.add(new UserOrder());
		orders.add(new UserOrder());
		when(userRepository.findByUsername("testUser")).thenReturn(user);
		when(orderRepository.findByUser(user)).thenReturn(orders);
		ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("testUser");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		List<UserOrder> responseOrders = responseEntity.getBody();
		assertEquals(orders.size(), responseOrders.size());
	}

}
