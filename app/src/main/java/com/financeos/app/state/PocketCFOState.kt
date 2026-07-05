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
import java.util.Calendar

class PocketCFOState {

    enum class AppScreen {
        DASHBOARD, ASSETS, ADD_ACCOUNT, ADD_EXPENSE, ADD_INCOME, ACCOUNT_DETAILS, MONTHLY_DASHBOARD
    }

    var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
    var selectedAccountId by mutableStateOf<String?>(null)
    var discoveryStatus by mutableStateOf("Ready")
    var messagesRead by mutableIntStateOf(0)
    var financialMessagesFound by mutableIntStateOf(0)
    var banks by mutableStateOf<List<String>>(emptyList())
    var creditCards by mutableStateOf<List<CreditCard>>(emptyList())

    val transactions = mutableStateListOf<Transaction>()
    val accounts = mutableStateListOf<Account>()

    val totalIncome: Double get() = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense: Double get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val totalAssets: Double get() = accounts.filter { !it.isLiability }.sumOf { it.balance }
    val totalLiabilities: Double get() = accounts.filter { it.isLiability }.sumOf { it.balance }
    val netWorth: Double get() = totalAssets - totalLiabilities + totalIncome - totalExpense

    private val currentMonthTransactions: List<Transaction>
        get() {
            val cal = Calendar.getInstance()
            val currentMonth = cal.get(Calendar.MONTH)
            val currentYear = cal.get(Calendar.YEAR)

            return transactions.filter {
                val tCal = Calendar.getInstance()
                tCal.timeInMillis = it.timestamp
                tCal.get(Calendar.MONTH) == currentMonth && tCal.get(Calendar.YEAR) == currentYear
            }
        }

    val monthlyIncome: Double get() = currentMonthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val monthlyExpense: Double get() = currentMonthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val monthlySavings: Double get() = monthlyIncome - monthlyExpense
    val burnRate: Double get() {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        return if (day > 0) monthlyExpense / day else 0.0
    }

    fun openDashboard() { currentScreen = AppScreen.DASHBOARD; selectedAccountId = null }
    fun openAssets() { currentScreen = AppScreen.ASSETS; selectedAccountId = null }
    fun openAddAccount() { currentScreen = AppScreen.ADD_ACCOUNT }
    fun openAddExpense() { currentScreen = AppScreen.ADD_EXPENSE }
    fun openAddIncome() { currentScreen = AppScreen.ADD_INCOME }
    fun openAccountDetails(accountId: String) { selectedAccountId = accountId; currentScreen = AppScreen.ACCOUNT_DETAILS }
    fun openMonthlyDashboard() { currentScreen = AppScreen.MONTHLY_DASHBOARD }

    fun addAccount(name: String, type: AccountType, balance: Double, institution: String) {
        accounts.add(Account(name = name, type = type, balance = balance, institution = institution))
    }

    fun addExpense(amount: Double, category: String, notes: String, accountId: String? = null) {
        transactions.add(0, Transaction(amount = amount, category = category, notes = notes, type = TransactionType.EXPENSE, accountId = accountId))
    }

    fun addIncome(amount: Double, source: String, notes: String, accountId: String? = null) {
        transactions.add(0, Transaction(amount = amount, category = source, notes = notes, type = TransactionType.INCOME, accountId = accountId))
    }

    fun startDiscovery() { discoveryStatus = "Scanning..." }
    fun discoveryCompleted(totalMessages: Int, financialMessages: Int, banksFound: List<String>, cardsFound: List<CreditCard>) {
        messagesRead = totalMessages; financialMessagesFound = financialMessages; banks = banksFound; creditCards = cardsFound; discoveryStatus = "Completed"
    }
    fun deleteTransaction(transaction: Transaction) {
        transactions.remove(transaction)
    }
}