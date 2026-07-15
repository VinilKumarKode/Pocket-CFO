package com.financeos.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialEntityDao {

    @Query("SELECT * FROM financial_entities ORDER BY name ASC")
    fun getAllEntities(): Flow<List<FinancialEntity>>

    @Query("SELECT * FROM financial_entities WHERE name = :bankName AND lastFourDigits = :lastFour LIMIT 1")
    suspend fun getEntityByDetails(bankName: String, lastFour: String?): FinancialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Overwrites duplicate cards
    suspend fun insertEntity(entity: FinancialEntity)

    @Update
    suspend fun updateEntity(entity: FinancialEntity)
}