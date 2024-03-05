package com.spring.rewards.entity;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	@Id
	private Long empId;
	
	private String empName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Employee parent;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
	private List<Employee> childern =new ArrayList<>(); 
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "employee_roles", joinColumns = @JoinColumn(name = "emp_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;
	
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name="user_id")
	private User user;
	
	
	@OneToMany(fetch= FetchType.EAGER,mappedBy="parent",cascade= CascadeType.ALL)
	private List<Rewards> rewards= new ArrayList<>();
	
	@OneToMany(fetch=FetchType.EAGER,mappedBy="parent",cascade=CascadeType.ALL)
	private List<TeamRewards> teamRewards= new ArrayList<>();
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="team_id")
	private Team team;
	
	private String tescoId;
	
	  public void addReward(Rewards reward) {
	        this.rewards.add(reward);
	        reward.setParent(this); // Assuming you have a setParent method in Rewards class
	    }
	  
	  public void addTeamReward(TeamRewards teamReward) {
		  this.teamRewards.add(teamReward);
		  teamReward.setParent(this);
	  }
		public Employee(User user, Set<Role> roles) {

			this.user=user;
			this.roles=roles;
		}

		@Override
		public String toString() {
			return "Employee [empId=" + empId + ", empName=" + empName + ", childern=" + childern
					+ ", roles=" + roles + ", user=" + user + ", rewards=" + rewards + "]";
		}
	

}
