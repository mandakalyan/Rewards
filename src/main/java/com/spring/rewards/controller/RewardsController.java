package com.spring.rewards.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.rewards.entity.Rewards;
import com.spring.rewards.entity.RewardsDropDown;
import com.spring.rewards.services.RewardsService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class RewardsController {
	
	@Autowired
	private RewardsService rewardsService;
	
	
	@PostMapping("submit/{empId}")
	public ResponseEntity<Object> submitRewards(@PathVariable Long empId, @RequestBody Rewards rewards) {
	    try {
	        Rewards reward = rewardsService.submitRewards(empId, rewards);
	        if (reward != null) {
	            return ResponseEntity.ok(reward);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (EntityNotFoundException  e) {
	        e.printStackTrace(); 
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	@PutMapping("/approveOrReject/{empId}")
    public ResponseEntity<Object> approveOrRejectRewards(
            @PathVariable Long empId,
            @RequestBody Map<String, Object> requestBody) {

        try {
        	
            long rewardId = ((Number) requestBody.get("id")).longValue();
            boolean approve = (boolean) requestBody.get("approve");

            Rewards reward = rewardsService.approveOrRejectRewards(empId, rewardId, approve);
            
            if (reward != null) {
                return ResponseEntity.ok(reward);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	

    @GetMapping("/requests/{empId}")
    public ResponseEntity<Object> getRewardRequestsForParent(@PathVariable Long empId) {

        try {
            List<Rewards> rewardRequests = rewardsService.getRewardRequestsForParent(empId);

            if (!rewardRequests.isEmpty()) {
                return ResponseEntity.ok(rewardRequests);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    
    @GetMapping("/all")
    public List<RewardsDropDown> getAllRewardsDropDown() {
        return rewardsService.getAllRewardsDropDown();
    }
    
    
    
     
    @GetMapping("/myrequests/{empId}")
    public ResponseEntity<Object> getRewardRequestsForUser(@PathVariable Long empId) {

        try {
            List<Rewards> rewardRequests = rewardsService.getRewardsForUser(empId);

            if (!rewardRequests.isEmpty()) {
                return ResponseEntity.ok(rewardRequests);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/getname/{empId}")
    public String getEmpName(@PathVariable Long empId) {
    	return rewardsService.getEmpName(empId);
    }
    
    
    
    
    

}
