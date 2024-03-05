package com.spring.rewards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Reconciliation {
    
	@Id
	@GeneratedValue
	Long id;
	Long employeeNumber;
	String tescoEmployeeNumber;
	Float otlBookedHours;
	Float timexBookedHours;
	Float differenceInHours;
	String status;
	String empName;
	String periodName;
	String weekNumber;
	String yearNumber;
	
}
