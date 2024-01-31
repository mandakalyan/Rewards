package com.spring.rewards.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.rewards.entity.Team;
import com.spring.rewards.entity.TeamRewards;
import com.spring.rewards.services.TeamRewardsService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class TeamRewardsController {

	
	@Autowired
	private TeamRewardsService teamRewardService;
	
	
	
	
	
	@PostMapping("submitTeam/{empId}")
	public ResponseEntity<Object> submitTeamRewards(@PathVariable  Long empId, @RequestBody TeamRewards Trewards){
		
		try {
	       TeamRewards tReward= teamRewardService.submitTeamRewards(empId, Trewards);
	       if(tReward!=null) {
	    	   return ResponseEntity.ok(tReward);
	    	   
	       }else {
	    	   return ResponseEntity.notFound().build();
	       }
		}catch(EntityNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	
	@PutMapping("/approveOrRejectForTeam/{empId}")
	public ResponseEntity<Object> approveOrRejectTeamRewards(@PathVariable Long empId, @RequestBody Map<String,Object>requestBody){
		try {
			Long teamRewardId=((Number)requestBody.get("teamRewardId")).longValue();
			
	        boolean approve = Boolean.parseBoolean(requestBody.get("approve").toString());
			
			TeamRewards teamRewards= teamRewardService.approveOrRejectRewards(empId, teamRewardId, approve);
			
			if(teamRewards!=null) {
				return ResponseEntity.ok().build();
				
				
			}else {
				return ResponseEntity.notFound().build();
			}
		}catch(EntityNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			
				
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
			}
		    
		}
	@GetMapping("/teamRequests/{empId}")
	public ResponseEntity<Object> getTeamRewardrequestsForParent(@PathVariable Long empId){
		try {
			List<TeamRewards> teamRewardRequests= teamRewardService.getTeamRewardRequestsforParent(empId);
			if(!teamRewardRequests.isEmpty()) {
				return ResponseEntity.ok(teamRewardRequests);
				
			}else {
				return ResponseEntity.notFound().build();
			}
		}catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }}
		
		
		@GetMapping("/getAllTeamRewards")
		public ResponseEntity<Object>getAllTeamRewards(){
			try {
				List<TeamRewards>teamRewards  =teamRewardService.getAllTeamRewards();
				if(!teamRewards.isEmpty()) {
					return ResponseEntity.ok(teamRewards);
					
				}else {
					return ResponseEntity.notFound().build();
				}
			}catch(EntityNotFoundException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			}catch (Exception e) {
	            e.printStackTrace(); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		}
		
		
		
		@GetMapping("/allTeams")
		public ResponseEntity<Object> getAllTeams(){
			try {
				List<Team> teams =teamRewardService.getAllTeams();
				if(!teams.isEmpty()) {
					return ResponseEntity.ok(teams);
					
				}else {
					return ResponseEntity.notFound().build();
				}
			}catch(EntityNotFoundException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			}catch (Exception e) {
	            e.printStackTrace(); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			
		}
		
		}
		
}
	
	
	
	
	
	
	
	
	
	

