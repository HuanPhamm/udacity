package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserRequest {

	@JsonProperty
	@NotBlank(message = "username do not bank")
	private String username;
	@JsonProperty
	@NotBlank(message = "password do not bank")
	@Size(min = 8, max = 16, message = "password character min {min} and {max}")
	private String password;

	@JsonProperty
	@NotBlank(message = "confirmPassword do not bank")
	@Size(min = 8, max = 16, message = "confirmPassword character min {min} and {max}")
	private String confirmPassword;

	@AssertTrue(message = "confirmPassword do not same password")
	private boolean isConfirmPassword(){
		if(!StringUtils.isEmpty(password) && !StringUtils.isEmpty(confirmPassword)){
			if(!password.equals(confirmPassword)){
				return false;
			}
		}
		return true;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
