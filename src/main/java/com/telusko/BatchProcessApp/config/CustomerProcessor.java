package com.telusko.BatchProcessApp.config;

import org.springframework.batch.item.ItemProcessor;

import com.telusko.BatchProcessApp.dao.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer item) throws Exception {
		
		//Logic to process data or filer or perform operation...
		
		
		
		return item;
	}
	
	
	

}
