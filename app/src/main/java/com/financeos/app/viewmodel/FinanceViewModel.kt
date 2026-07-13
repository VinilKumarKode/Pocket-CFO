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

    // The Timeline Engine (Active Bills)
    val upcomingBills = db.upcomingLiabilityDao().getPendingLiabilities()

    // --- THE SOFT-DELETED TRASH STREAM ---
    val trashedBills = db.upcomingLiabilityDao().getTrashedLiabilities()
}