package com.spring.rewards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TeamRewards {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="team_reward_id")
	private Long teamRewardId;
	
	private long empId;
	
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "trddId")
    private TeamRewardsDropDown teamDropDown;
   
    @JoinColumn(name="team_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Team team;
    
    private String comments;
	private String status;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_emp_id")
    @JsonIgnore
	private Employee parent;
	
	private String  empName;

}
