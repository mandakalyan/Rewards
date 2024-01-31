package com.spring.rewards.controller;

import java.util.HashSet;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spring.rewards.Repository.EmployeeRepository;
import com.spring.rewards.Repository.RoleRepository;
import com.spring.rewards.Repository.UserRepository;
import com.spring.rewards.entity.ERole;
import com.spring.rewards.entity.Employee;
import com.spring.rewards.entity.Role;
import com.spring.rewards.entity.User;
import com.spring.rewards.request.LoginRequest;
import com.spring.rewards.request.ResetPassword;
import com.spring.rewards.request.SignupRequest;
import com.spring.rewards.response.JwtResponse;
import com.spring.rewards.response.MessageResponse;
import com.spring.rewards.security.jwt.JwtUtils;
import com.spring.rewards.security.services.UserDetailsImpl;
import com.spring.rewards.services.AuthService;



@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController
{
	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
    EmployeeRepository employeeRepository;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	AuthService userService;
	
//	@PostMapping("/signup")
//	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest)
//	
//	{
//		if (userRepo.existsByEmail(signupRequest.getEmail())) {
//			return ResponseEntity.badRequest().body(new MessageResponse("Email Already Used"));
//		}
//		
//		Set<String> strRoles =signupRequest.getRoles();
//		Set<Role> roles =new HashSet<>();
//		if(strRoles == null) {
//			Role userRole= roleRepository.findByName(ERole.ROLE_EMPLOYEE)
//					.orElseThrow(()-> new RuntimeException("Error:Role Not Found"));
//			roles.add(userRole);
//		}
//		else {
//			strRoles.forEach(role -> {
//				if (role.equals("manager")) {
//					Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(managerRole);
//
//				}
//				else if(role.equals("admin")) {
//					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//							.orElseThrow(()-> new RuntimeException("Error: Role is not found."));
//					roles.add(adminRole);
//				}
//				
//			});
//		}
//		User user = new User (signupRequest.getEmail(),encoder.encode(signupRequest.getPassword()));
//		Employee employee =new Employee(user,roles);
//	    employee.setRoles((Set<Role>) roles); // Casting to Set<Role>
//		userRepo.save(employee);
//
//		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//					
//		}
//		
	
	@PostMapping("/signin")
		public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest)

		{
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<GrantedAuthority> role = userDetails.getAuthority();
			User employee = userRepo.findByEmail(loginRequest.getEmail());
        	// System.out.println(employee);
			employee.setActiveStatus(true);
			userRepo.save(employee);
			return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getEmpId(), userDetails.getEmail(), role));
		}
	
	@PostMapping("/reset")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPassword resetPassword){
		   try {
			   userService.resetPassword(resetPassword.getEmail(),resetPassword.getCurrentPassword(),resetPassword.getNewPassword());
			   return ResponseEntity.ok("password reset susccessfully");
		   }catch(Exception e) {
			   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		   }
		   
	   }
	
	}


  
	
	
