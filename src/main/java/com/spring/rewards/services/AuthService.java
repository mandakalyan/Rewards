package com.spring.rewards.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.rewards.Repository.UserRepository;
import com.spring.rewards.entity.User;

@Service
public class AuthService {
	
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public void resetPassword(String email, String currentPassword,String newPassword) throws Exception {
		User user = userRepo.findByEmail(email);
		
		if(user!=null && passwordEncoder.matches(currentPassword,user.getPassword())){
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepo.save(user);
		}else {
			throw new Exception("Invalid current password");
		}
	}
}
