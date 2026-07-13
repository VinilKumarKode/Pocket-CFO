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

    @Query("SELECT * FROM upcoming_liabilities WHERE isPaid = 0 AND isDeleted = 0 ORDER BY dueDate ASC")
    fun getPendingLiabilities(): Flow<List<UpcomingLiability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiability(liability: UpcomingLiability)

    @Update
    suspend fun updateLiability(liability: UpcomingLiability)

    @Query("SELECT * FROM upcoming_liabilities WHERE title = :title AND amountDue = :amount AND isPaid = 0 AND isDeleted = 0 LIMIT 1")
    suspend fun findPendingLiability(title: String, amount: Double): UpcomingLiability?

    @Delete
    suspend fun deleteLiability(liability: UpcomingLiability)

    @Delete
    suspend fun hardDeleteLiability(liability: UpcomingLiability)

    @Query("DELETE FROM upcoming_liabilities WHERE isDeleted = 1 AND deletedAt <= :threshold")
    suspend fun purgeOldTrashedLiabilities(threshold: Long)

    // --- THE RECOVERY PIPELINE QUERY ---
    // Streams items currently sitting in the trash bin sorted by most recently deleted
    @Query("SELECT * FROM upcoming_liabilities WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getTrashedLiabilities(): Flow<List<UpcomingLiability>>
}