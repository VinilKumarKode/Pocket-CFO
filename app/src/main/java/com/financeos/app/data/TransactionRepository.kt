package com.financeos.app.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    // Fetch the live list of all transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    // Fetch transactions that need statement verification
    val unreconciledTransactions: Flow<List<Transaction>> = transactionDao.getUnreconciledTransactions()

    // Insert a new transaction into the ledger
    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Update an existing record
    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    // Delete a record if a mistake was made
    suspend fun delete(transactionId: Int) {
        transactionDao.deleteTransactionById(transactionId)
    }
}