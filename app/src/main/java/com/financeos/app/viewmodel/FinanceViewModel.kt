package com.financeos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.financeos.app.data.FinanceDatabase

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FinanceDatabase.getDatabase(application)

    // The transaction ledger
    val transactions = db.transactionDao().getAllTransactions()

    // The Discovered Financial Profile
    val financialEntities = db.financialEntityDao().getAllEntities()

    // --- THE MISSING PIECE: The Timeline Engine ---
    // This allows the Dashboard to see the bills we saved in the database!
    val upcomingBills = db.upcomingLiabilityDao().getPendingLiabilities()
}