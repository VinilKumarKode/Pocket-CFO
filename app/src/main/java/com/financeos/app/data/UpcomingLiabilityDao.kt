package com.financeos.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UpcomingLiabilityDao {
    // This grabs only the unpaid bills and sorts them so the most urgent one is at the top!
    @Query("SELECT * FROM upcoming_liabilities WHERE isPaid = 0 ORDER BY dueDate ASC")
    fun getPendingLiabilities(): Flow<List<UpcomingLiability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiability(liability: UpcomingLiability)

    @Update
    suspend fun updateLiability(liability: UpcomingLiability)

    // Helps prevent adding the exact same bill twice if the bank sends a reminder SMS
    @Query("SELECT * FROM upcoming_liabilities WHERE title = :title AND amountDue = :amount AND isPaid = 0 LIMIT 1")
    suspend fun findPendingLiability(title: String, amount: Double): UpcomingLiability?
}