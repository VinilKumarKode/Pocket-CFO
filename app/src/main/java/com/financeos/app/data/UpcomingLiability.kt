package com.financeos.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_liabilities")
data class UpcomingLiability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,            // e.g., "HDFC Credit Card Bill" or "Home Loan EMI"
    val amountDue: Double,
    val dueDate: Long,            // The exact timestamp it needs to be paid by
    val type: String,             // e.g., "CREDIT_CARD_BILL", "EMI", "UTILITY"
    val isPaid: Boolean = false,  // So you can check it off when you pay it!
    val rawMessage: String = ""
)