package com.financeos.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Version bumped to 3, and UpcomingLiability added to the list of entities!
@Database(
    entities = [Transaction::class, FinancialEntity::class, UpcomingLiability::class],
    version = 3,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun financialEntityDao(): FinancialEntityDao
    abstract fun upcomingLiabilityDao(): UpcomingLiabilityDao // The new DAO!

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                )
                    .fallbackToDestructiveMigration() // Safely wipes the V2 database to build V3
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}