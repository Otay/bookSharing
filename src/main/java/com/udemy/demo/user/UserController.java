package com.udemy.demo.user;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.demo.jwt.JwtController;
import com.udemy.demo.jwt.JwtFilter;
import com.udemy.demo.jwt.JwtUtils;

@RestController
public class UserController {

	@Autowired
	UserRepository userRrepository;

	@Autowired
	JwtController jwtController;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@PostMapping(value = "users")
	ResponseEntity addUser(@Valid @RequestBody UserInfo user) {

		UserInfo ExistingUser = userRrepository.findOneByEmail(user.getEmail());

		if (ExistingUser != null) {
			return new ResponseEntity("mail already exists!", HttpStatus.BAD_REQUEST);
		}

		UserInfo savedUser = saveUser(user);

		Authentication authentication = jwtController.logUser(user.getEmail(), user.getPassword());

		String jwt = jwtUtils.generateToken(authentication);

		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

		return new ResponseEntity(user, headers, HttpStatus.OK);

	}

	@GetMapping(value = "/isConnected")
	public ResponseEntity getConnectedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return new ResponseEntity(((UserDetails) principal).getUsername(), HttpStatus.OK);
		}
		return new ResponseEntity("User is not connected", HttpStatus.FORBIDDEN);
	}

	private UserInfo saveUser(UserInfo userInfo) {
		UserInfo user = new UserInfo();
		user.setEmail(userInfo.getEmail());
		user.setFirstName(StringUtils.capitalize(userInfo.getFirstName()));
		user.setLastName(StringUtils.capitalize(userInfo.getLastName()));
		user.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
		System.out.println("password encrypted : " + bCryptPasswordEncoder.encode(user.getPassword()));
		userRrepository.save(user);
		return user;

	}

}
