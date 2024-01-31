package com.spring.rewards.request;

import lombok.Data;

@Data 
public class ResetPassword {
	
	String email;
	String currentPassword;
	String newPassword;
	
}
