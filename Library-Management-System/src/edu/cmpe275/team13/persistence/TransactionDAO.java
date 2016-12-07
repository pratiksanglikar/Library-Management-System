package edu.cmpe275.team13.persistence;

import edu.cmpe275.team13.beans.Transaction;

public interface TransactionDAO {
	public void performTransaction(Transaction transaction);
	public int getTodaysTransaction(int patron_id);
}
