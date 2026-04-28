package com.bank.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bank.model.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


@Repository
public class CustomerRepository {
	
	@PersistenceContext
    private EntityManager entityManager;

	 @Transactional
	    public Customer save(Customer customer) {
	        if (customer.getCustomerId() == null) {
	            entityManager.persist(customer);
	            return customer;
	        }
	        return entityManager.merge(customer);
	    }

	public boolean existsByEmail(String email) {
		Long count = entityManager.createQuery(
				"SELECT COUNT(c) FROM Customer c WHERE c.email = :email",
				Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count > 0;
	}

	public boolean existsByMobile(String mobile) {
		Long count = entityManager.createQuery(
				"SELECT COUNT(c) FROM Customer c WHERE c.mobile = :mobile",
				Long.class)
				.setParameter("mobile", mobile)
				.getSingleResult();
		
		return count > 0;
	}

	public Customer findAccountNumber(String accountNumber) {
		try {
			return entityManager.
					createQuery("Select c From Customer c Where c.accountNumber = :acc",
							Customer.class)
					.setParameter("acc", accountNumber)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Customer> findAll() {

		return entityManager.createQuery(
				"Select c from Customer c",Customer.class)
				.getResultList();
	}

	public Customer findById(Long id) {
        return entityManager.find(Customer.class, id);
    }
		

}
