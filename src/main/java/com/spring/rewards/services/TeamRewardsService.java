package com.spring.rewards.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.rewards.Repository.EmployeeRepository;
import com.spring.rewards.Repository.TeamRepository;
import com.spring.rewards.Repository.TeamRewardsRepository;
import com.spring.rewards.entity.Employee;
import com.spring.rewards.entity.Team;
import com.spring.rewards.entity.TeamRewards;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TeamRewardsService {
	
	@Autowired
	EmployeeRepository empRepo;
	
	@Autowired
	TeamRewardsRepository tRepo;
	
	@Autowired
	TeamRepository teamRepo;
	
	public TeamRewards submitTeamRewards(long empId, TeamRewards tRewards) {
		Optional<Employee> employeeOptional= empRepo.findById(empId);
		if(employeeOptional.isEmpty()) {
			throw new EntityNotFoundException("employee with" +empId+"doesnot exist");
		}
	       Employee childEmployee= employeeOptional.get();
	       
	       Employee parentEmployee= childEmployee.getParent();
	       if(parentEmployee==null) {
	    	   throw new EntityNotFoundException("parent does not exit for employee"+empId);
	    	   
	       }
	       TeamRewards teamRewards=new TeamRewards();
	       teamRewards.setEmpId(empId);
	       teamRewards.setTeamDropDown(tRewards.getTeamDropDown());
	       teamRewards.setComments(tRewards.getComments());
	       teamRewards.setStatus("Pending");
	       teamRewards.setTeam(tRewards.getTeam());
	       parentEmployee.addTeamReward(teamRewards);
	       teamRewards.setEmpName(childEmployee.getEmpName());
	       empRepo.save(parentEmployee);
	       
	       return teamRewards;
	       

}
	public TeamRewards approveOrRejectRewards(long empId,Long tRewardId,boolean approve) {
		Optional<Employee> employeeOptional= empRepo.findById(empId);
		
		if(employeeOptional.isEmpty()) {
			throw new EntityNotFoundException("Employee with id"+empId+"does not exist:");
			
		}
       Employee parentEmployee =employeeOptional.get();
       
       Optional<TeamRewards> tRewardsOptional = parentEmployee.getTeamRewards().stream().filter(treward-> treward.getTeamRewardId()==tRewardId)
    		   .findFirst();
       
       if(tRewardsOptional.isEmpty()) {
    	   throw new EntityNotFoundException("Team_Reward with id"+tRewardId+"does not exist for the employee with id"+empId);
    	   
       }
       
       TeamRewards tReward=tRewardsOptional.get();
       tReward.setStatus(approve? "Approved":"Rejected");
       
       empRepo.save(parentEmployee);
	   
       return tReward;
	}
	
	public List<TeamRewards> getTeamRewardRequestsforParent(long empId){
		Optional<Employee>employeeOptional =empRepo.findById(empId);
		
		if(employeeOptional.isEmpty()) {
			throw new EntityNotFoundException("Employee with id "+empId+"does not exit.");
		}
		Employee parentEmployee = employeeOptional.get();
		return parentEmployee.getTeamRewards().stream()
				.filter(tReward -> "Pending".equals(tReward.getStatus()))
				.collect(Collectors.toList());
	}
	
	public List<TeamRewards> getAllTeamRewards(){
		return tRepo.findAll();
	}
	
     public List<Team>getAllTeams(){
    	 return teamRepo.findAll();
    	 
     }
     
     public List<TeamRewards> getTeamRewardsForTeams(){
			return tRepo.findAll();
			
		}
     
     
     public String getTeamName(Long empId) {
    	 Optional<Employee> employee= empRepo.findById(empId);
    	 if(employee.isPresent()) {
    	 String teamName=employee.get().getTeam().getTeamName();
    	 return teamName;
    	 }
    	 else {
    		 return "employee not found";
    	 }
     }

}
