package com.financeos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financeos.app.data.Transaction
import com.financeos.app.data.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: TransactionRepository) : ViewModel() {

    // Converts the database flow into a state the UI can easily read
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    fun removeTransaction(transactionId: Int) {
        viewModelScope.launch {
            repository.delete(transactionId)
        }
    }
}

// A factory to help Android build this specific ViewModel
class FinanceViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}