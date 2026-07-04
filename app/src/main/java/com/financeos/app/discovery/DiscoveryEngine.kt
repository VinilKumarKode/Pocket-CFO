package com.financeos.app.discovery

import android.content.Context
import android.util.Log
import com.financeos.app.discovery.detector.BankDetector
import com.financeos.app.discovery.detector.CreditCardDetector
import com.financeos.app.discovery.detector.FinancialMessageDetector
import com.financeos.app.discovery.sms.SmsReader
import com.financeos.app.models.DiscoveryResult

class DiscoveryEngine {

    private val smsReader = SmsReader()

    private val financialDetector = FinancialMessageDetector()

    private val bankDetector = BankDetector()

    private val creditCardDetector = CreditCardDetector()

    fun discoverFinancialMessages(
        context: Context
    ): DiscoveryResult {

        val messages = smsReader.readMessages(context)

        val financialMessages = messages.filter {

            financialDetector.isFinancial(it)

        }

        val banks = financialMessages
            .mapNotNull {

                bankDetector.detect(
                    it.sender,
                    it.body
                )

            }
            .distinct()
            .sorted()

        val creditCards = financialMessages
            .mapNotNull {

                creditCardDetector.detect(
                    it.sender,
                    it.body
                )

            }
            .distinct()

        Log.d(
            "PocketCFO",
            "Messages Read = ${messages.size}"
        )

        Log.d(
            "PocketCFO",
            "Financial Messages = ${financialMessages.size}"
        )

        Log.d(
            "PocketCFO",
            "Banks Found = $banks"
        )

        Log.d(
            "PocketCFO",
            "Credit Cards Found = $creditCards"
        )

        return DiscoveryResult(

            messagesRead = messages.size,

            financialMessages = financialMessages.size,

            banks = banks,

            creditCards = creditCards

        )

    }

}