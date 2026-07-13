package com.financeos.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_liabilities")
data class UpcomingLiability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amountDue: Double,
    val dueDate: Long,
    val type: String,
    val isPaid: Boolean = false,
    val rawMessage: String = "",

    // --- THE NEW AUDIT TRAIL FIELDS (TRASH BIN) ---
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)