package com.financeos.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// NOTICE: We added FinancialEntity::class and changed version to 2
@Database(entities = [Transaction::class, FinancialEntity::class], version = 2, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun financialEntityDao(): FinancialEntityDao // The new Dao!

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                // fallbackToDestructiveMigration() wipes the old V1 database safely so V2 can build
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}