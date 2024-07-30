package com.telusko.BatchProcessApp.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.telusko.BatchProcessApp.dao.Customer;
import com.telusko.BatchProcessApp.dao.ICustomerRepo;

import jakarta.transaction.TransactionManager;

@Configuration
public class SpringBatchConfig {

	@Autowired
	private ICustomerRepo repo;
	
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	//item reader
	
	@Bean
	public FlatFileItemReader<Customer> customerReader()
	{
		FlatFileItemReader<Customer> itemReader=new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		
		
		return itemReader;
	}
	
	private LineMapper<Customer> lineMapper() 
	{
		
		DefaultLineMapper<Customer> lineMapper=new DefaultLineMapper<>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
		
		
		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);
		
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		
		return lineMapper;
	}

	//item processor
	
	@Bean
	public CustomerProcessor processCxData()
	{
		return new CustomerProcessor();
	}
	
	//item writer
	@Bean
	public RepositoryItemWriter<Customer> writeCustomer()
	{
		RepositoryItemWriter<Customer> itemWriter=new RepositoryItemWriter<>();
		itemWriter.setRepository(repo);
		itemWriter.setMethodName("save");
		
		return itemWriter;
	}
	
	
	//step
	@Bean
	public Step step()
	{
		return new StepBuilder("step-1",jobRepository).
				<Customer, Customer>chunk(10, transactionManager)
				.reader(customerReader())
				.processor(processCxData())
				.writer(writeCustomer())
				.build();
	}
	
	//job
	@Bean
	public Job job()
	{
		return new JobBuilder("customers-import", jobRepository).start(step())
				.build();
	}
	
	
	
}






