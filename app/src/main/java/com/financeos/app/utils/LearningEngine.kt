package com.financeos.app.utils

import com.financeos.app.data.Transaction
import com.financeos.app.data.TransactionDao

object LearningEngine {

    fun applyUserMemory(newTx: Transaction, pastTransactions: List<Transaction>): Transaction {
        // 1. Only look at past transactions that you have explicitly approved or edited
        val approvedHistory = pastTransactions.filter { it.isReconciled }

        // 2. Try to find a past transaction from the exact same sender (e.g., AD-HDFCBK)
        // that shares a key merchant word in the raw text.
        // We use the first word of the parsed merchant to find similarities.
        val searchKeyword = newTx.description.split(" ").firstOrNull() ?: ""

        if (searchKeyword.length > 2) {
            val matchedMemory = approvedHistory.find { pastTx ->
                pastTx.sender == newTx.sender &&
                        pastTx.rawMessage.contains(searchKeyword, ignoreCase = true)
            }

            // 3. If we found a memory, overwrite the app's guesses with YOUR actual past edits!
            if (matchedMemory != null) {
                return newTx.copy(
                    category = matchedMemory.category,
                    description = matchedMemory.description, // Uses the clean name you typed last time
                    paymentMethod = matchedMemory.paymentMethod // Remembers the exact card/account
                )
            }
        }

        // If no memory exists yet, return it as-is so you can teach it.
        return newTx
    }
}