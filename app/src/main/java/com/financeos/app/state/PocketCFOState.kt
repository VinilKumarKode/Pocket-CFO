package com.financeos.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.financeos.app.models.Account
import com.financeos.app.models.AccountType
import com.financeos.app.models.CreditCard
import com.financeos.app.models.Transaction
import com.financeos.app.models.TransactionType

class PocketCFOState {

    enum class AppScreen {
        DASHBOARD,
        ASSETS,
        ADD_ACCOUNT,
        ADD_EXPENSE,
        ADD_INCOME
    }

    var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
        private set

    var discoveryStatus by mutableStateOf("Ready")
        private set

    var messagesRead by mutableIntStateOf(0)
        private set

    var financialMessagesFound by mutableIntStateOf(0)
        private set

    var banks by mutableStateOf<List<String>>(emptyList())
        private set

    var creditCards by mutableStateOf<List<CreditCard>>(emptyList())
        private set

    val transactions = mutableStateListOf<Transaction>()

    val accounts = mutableStateListOf<Account>()

    val totalIncome: Double
        get() = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    val totalExpense: Double
        get() = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    val netWorth: Double
        get() = totalIncome - totalExpense

    fun openDashboard() {
        currentScreen = AppScreen.DASHBOARD
    }

    fun openAssets() {
        currentScreen = AppScreen.ASSETS
    }

    fun openAddAccount() {
        currentScreen = AppScreen.ADD_ACCOUNT
    }

    fun openAddExpense() {
        currentScreen = AppScreen.ADD_EXPENSE
    }

    fun openAddIncome() {
        currentScreen = AppScreen.ADD_INCOME
    }

    fun addAccount(
        name: String,
        type: AccountType,
        balance: Double,
        institution: String
    ) {

        accounts.add(

            Account(

                name = name,

                type = type,

                balance = balance,

                institution = institution

            )

        )

    }

    fun addExpense(
        amount: Double,
        category: String,
        notes: String
    ) {

        transactions.add(

            0,

            Transaction(

                amount = amount,

                category = category,

                notes = notes,

                type = TransactionType.EXPENSE

            )

        )

    }

    fun addIncome(
        amount: Double,
        source: String,
        notes: String
    ) {

        transactions.add(

            0,

            Transaction(

                amount = amount,

                category = source,

                notes = notes,

                type = TransactionType.INCOME

            )

        )

    }

    fun startDiscovery() {
        discoveryStatus = "Scanning..."
    }

    fun discoveryCompleted(
        totalMessages: Int,
        financialMessages: Int,
        banksFound: List<String>,
        cardsFound: List<CreditCard>
    ) {

        messagesRead = totalMessages
        financialMessagesFound = financialMessages
        banks = banksFound
        creditCards = cardsFound
        discoveryStatus = "Completed"

    }

}