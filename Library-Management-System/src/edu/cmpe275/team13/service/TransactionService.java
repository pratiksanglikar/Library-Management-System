package edu.cmpe275.team13.service;

import java.util.List;

import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.Transaction;

public interface TransactionService {
	
	public void performTransaction(Transaction transaction);
	public List<IssueBook> getPendingBooks(int patron_id);
}
