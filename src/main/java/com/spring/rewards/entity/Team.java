package com.spring.rewards.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Team {
	@Id
	private String teamId;
	private String teamName;
	
	
	@OneToMany(mappedBy ="team",fetch = FetchType.LAZY)
	private List<Employee> employees= new ArrayList<>();
	
	 @JsonIgnore
	    public List<Employee> getEmployees() {
	        return employees;
	    }
    
	

}