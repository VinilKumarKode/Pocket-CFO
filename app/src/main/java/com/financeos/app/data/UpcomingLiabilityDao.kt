package com.financeos.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UpcomingLiabilityDao {

    // --- UPDATED: Only grabs bills that are unpaid AND not in the trash ---
    @Query("SELECT * FROM upcoming_liabilities WHERE isPaid = 0 AND isDeleted = 0 ORDER BY dueDate ASC")
    fun getPendingLiabilities(): Flow<List<UpcomingLiability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiability(liability: UpcomingLiability)

    @Update
    suspend fun updateLiability(liability: UpcomingLiability)

    // --- UPDATED: Prevents duplicate syncing, but ignores things you already trashed ---
    @Query("SELECT * FROM upcoming_liabilities WHERE title = :title AND amountDue = :amount AND isPaid = 0 AND isDeleted = 0 LIMIT 1")
    suspend fun findPendingLiability(title: String, amount: Double): UpcomingLiability?

    // We keep the hard delete just in case we need to permanently empty the trash later!
    @Delete
    suspend fun hardDeleteLiability(liability: UpcomingLiability)
}