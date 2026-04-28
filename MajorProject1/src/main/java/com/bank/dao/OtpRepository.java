package com.bank.dao;

import org.springframework.stereotype.Repository;

import com.bank.model.Otp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class OtpRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void save(Otp entity) {
		entityManager.persist(entity);
	}

	public Otp findValidOtp(String accountNumber, String otp) {
	try {
		return entityManager.createQuery(
				"SELECT o from Otp o where o.accountNumber= :acc and o.otp= :otp",
				Otp.class)
				.setParameter("acc", accountNumber)
				.setParameter("otp", otp)
				.getSingleResult();	
	}catch(Exception e) {
		return null;
	}
   }

	@Transactional
	public void delete(Otp otp2) {
		
		entityManager.remove(
				entityManager.contains(otp2) ? otp2 : entityManager.merge(otp2));	
	}

	
	

}
