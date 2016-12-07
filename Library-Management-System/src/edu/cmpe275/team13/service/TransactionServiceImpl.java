package edu.cmpe275.team13.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cmpe275.team13.beans.Transaction;
import edu.cmpe275.team13.exceptions.TransactionLimitExceededException;
import edu.cmpe275.team13.persistence.TransactionDAO;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionDAO transacationDAO;

	@Override
	public void performTransaction(Transaction transaction) {
		if (transaction.isCheckout()) {
			if (transaction.getBooks().size() > 5) {
				throw new TransactionLimitExceededException("Maximum 5 books can be checked out at a time!");
			}
			if (transaction.getPatron().getBooks_issued() + transaction.getBooks().size() > 10) {
				throw new TransactionLimitExceededException("Maximum number of books exceeded!");
			}
			System.out.println("Validating");
			int todaysTransaction = this.transacationDAO.getTodaysTransaction(transaction.getPatron().getPatron_id());
			if (todaysTransaction + transaction.getBooks().size() > 5) {
				throw new TransactionLimitExceededException("Only 5 books can be checked out in one day!");
			}
		} else {
			System.out.println("Validating!");
			if (transaction.getBooks().size() > 10) {
				throw new TransactionLimitExceededException("Maximum 10 books can be returned at a time!");
			}
		}
		this.transacationDAO.performTransaction(transaction);
	}
}
