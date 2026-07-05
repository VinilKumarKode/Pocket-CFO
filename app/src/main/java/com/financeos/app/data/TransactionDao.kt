package com.financeos.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Insert a new transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // Update an existing transaction (e.g., marking it as reconciled)
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // Delete a specific transaction
    @Query("DELETE FROM transactions_table WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Int)

    // Get a live stream of all transactions, sorted by most recent
    @Query("SELECT * FROM transactions_table ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // Identify transactions that haven't been verified against a statement
    @Query("SELECT * FROM transactions_table WHERE isReconciled = 0 ORDER BY date DESC")
    fun getUnreconciledTransactions(): Flow<List<Transaction>>

    // Calculate total rewards earned for a specific payment method
    @Query("SELECT SUM(rewardPointsEarned) FROM transactions_table WHERE paymentMethod = :method")
    fun getTotalRewardsForPaymentMethod(method: String): Flow<Double?>
}