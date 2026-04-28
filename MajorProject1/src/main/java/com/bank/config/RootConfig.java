package com.bank.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableJpaRepositories(basePackages = "com.bank.dao")
@EnableTransactionManagement
public class RootConfig {
	
	 @Bean
	    public DataSource dataSource() {
	        DriverManagerDataSource ds = new DriverManagerDataSource();
	        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
	        ds.setUrl("jdbc:mysql://localhost:3306/bank"); 
	        ds.setUsername("root");
	        ds.setPassword("Root_123");
	        return ds;
	    }
	
	 @Bean
	    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	        LocalContainerEntityManagerFactoryBean emf =
	                new LocalContainerEntityManagerFactoryBean();

	        emf.setDataSource(dataSource());
	        emf.setPackagesToScan("com.bank");
	        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

	        Properties props = new Properties();
	        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	        props.put("hibernate.hbm2ddl.auto", "update");
	        props.put("hibernate.show_sql", "true");

	        emf.setJpaProperties(props);
	        return emf;
	    }

	    @Bean
	    public JpaTransactionManager transactionManager() {
	        return new JpaTransactionManager(entityManagerFactory().getObject());
	    }

}
