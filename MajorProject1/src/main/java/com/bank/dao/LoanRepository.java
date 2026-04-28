package com.bank.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bank.model.Loan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class LoanRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void save(Loan loan) {
		
		if(loan.getLoanId() == null) {
			entityManager.persist(loan);
		}else {
			entityManager.merge(loan);
		}
	}

	public List<Loan> findByAccountNumber(String accountNumber) {
		 List<Loan> resultList = entityManager.createQuery(
				"Select l from Loan l where l.accountNumber = : acc",
				Loan.class)
				.setParameter("acc", accountNumber)
				.getResultList();
		 return resultList;
	}

	public List<Loan> findByStatus(String status) {
		return entityManager.createQuery(
				"Select l from Loan l where l.status = :status",
				Loan.class)
				.setParameter("status", status)
				.getResultList();
	}

	public Loan findByLoanId(Long loanId) {
		return entityManager.find(Loan.class, loanId);
	}
	
	

}
