package com.financeos.app.ui.screens.accounts

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.financeos.app.data.model.Account
import com.financeos.app.data.model.AccountType

class AccountViewModel : ViewModel() {

    val accounts = mutableStateListOf<Account>()

    init {

        accounts.add(
            Account(
                name = "SBI Savings",
                type = AccountType.SAVINGS,
                balance = 85000.0,
                bankName = "State Bank of India"
            )
        )

        accounts.add(
            Account(
                name = "Cash Wallet",
                type = AccountType.CASH,
                balance = 3200.0
            )
        )

    }

}