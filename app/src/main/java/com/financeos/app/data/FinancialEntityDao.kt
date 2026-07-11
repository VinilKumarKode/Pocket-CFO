package com.financeos.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialEntityDao {
    @Query("SELECT * FROM financial_entities")
    fun getAllEntities(): Flow<List<FinancialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: FinancialEntity): Long

    // This helps the app check if it already discovered this card before!
    @Query("SELECT * FROM financial_entities WHERE name = :name AND lastFourDigits = :lastFour LIMIT 1")
    suspend fun findEntity(name: String, lastFour: String?): FinancialEntity?
}