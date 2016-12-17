package edu.cmpe275.team13.persistence;

import java.util.List;

import edu.cmpe275.team13.beans.IssueBook;
import edu.cmpe275.team13.beans.Transaction;

public interface TransactionDAO {
	public void performTransaction(Transaction transaction);
	public int getTodaysTransaction(int patron_id);
	public List<IssueBook> getPendingBooks(int patron_id);
}
