package com.telusko.BatchProcessApp.dao;

import org.springframework.batch.core.step.builder.StepBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Customer {




	@Id
	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String gender;
	
	private String contactNo;
	
	private String country;
	
	private String dob;

}
