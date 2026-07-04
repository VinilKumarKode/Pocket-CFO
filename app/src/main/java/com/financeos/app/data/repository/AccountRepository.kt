package com.financeos.app.data.repository

import com.financeos.app.data.model.Account

class AccountRepository {

    private val accounts = mutableListOf<Account>()

    fun getAccounts(): List<Account> = accounts

    fun addAccount(account: Account) {
        accounts.add(account)
    }

    fun deleteAccount(id: String) {
        accounts.removeIf { it.id == id }
    }

    fun updateAccount(account: Account) {

        val index = accounts.indexOfFirst {
            it.id == account.id
        }

        if (index != -1) {
            accounts[index] = account
        }
    }
}