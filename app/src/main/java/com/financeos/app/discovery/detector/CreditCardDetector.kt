package com.financeos.app.discovery.detector

import com.financeos.app.models.CreditCard

class CreditCardDetector {

    fun detect(sender: String, body: String): CreditCard? {

        val text = (sender + " " + body).uppercase()

        val bank = when {

            text.contains("HDFC") -> "HDFC"

            text.contains("ICICI") -> "ICICI"

            text.contains("SBI") -> "SBI"

            text.contains("AXIS") -> "Axis"

            else -> return null

        }

        if (
            !text.contains("CARD") &&
            !text.contains("CREDIT")
        ) {
            return null
        }

        val last4 = Regex("\\d{4}")
            .find(body)
            ?.value ?: "----"

        return CreditCard(

            bank = bank,

            cardName = "Credit Card",

            last4 = last4

        )

    }

}