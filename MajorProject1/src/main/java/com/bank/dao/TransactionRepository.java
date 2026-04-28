package com.bank.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.bank.model.Transcation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;

@Repository
public class TransactionRepository {
	
	@PersistenceContext
	EntityManager entityManager;
	

	@Transactional
	public void save(Transcation transcation) {
		entityManager.persist(transcation);
	}


	public List<Transcation> findRecentTransactions(String acc) {
		return entityManager.createQuery("""
				Select t from Transcation t 
				Where t.fromAccount = :acc OR t.toAccount = :acc
				ORDER BY t.transactionDate DESC
				""",Transcation.class)
				.setParameter("acc", acc)
				.getResultList();
	}

	public List<Transcation> findByDateRange(String accNo, LocalDateTime start, LocalDateTime end) {
		return null;
	}

}
