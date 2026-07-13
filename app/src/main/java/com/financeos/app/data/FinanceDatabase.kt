package com.financeos.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Version bumped to 4 for the Soft Delete Architecture
@Database(
    entities = [Transaction::class, FinancialEntity::class, UpcomingLiability::class],
    version = 4,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun financialEntityDao(): FinancialEntityDao
    abstract fun upcomingLiabilityDao(): UpcomingLiabilityDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                // Safely wipes the old V3 database to build the new V4
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