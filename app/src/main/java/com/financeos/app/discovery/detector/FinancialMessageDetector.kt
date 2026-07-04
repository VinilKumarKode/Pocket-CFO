package com.financeos.app.discovery.detector

import com.financeos.app.models.FinancialMessage

class FinancialMessageDetector {

    private val keywords = listOf(

        "credited",
        "debited",
        "balance",
        "statement",
        "minimum due",
        "payment",
        "upi",
        "account",
        "loan",
        "emi",
        "interest",
        "credit card",
        "debit card",
        "available balance",
        "withdrawn",
        "spent",
        "received"

    )

    fun isFinancial(message: FinancialMessage): Boolean {

        val text = message.body.lowercase()

        return keywords.any {

            text.contains(it)

        }

    }

}

