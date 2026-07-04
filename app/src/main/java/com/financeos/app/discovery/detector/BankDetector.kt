package com.financeos.app.discovery.detector

class BankDetector {

    private val banks = listOf(
        "HDFC",
        "ICICI",
        "SBI",
        "AXIS",
        "KOTAK",
        "INDUSIND",
        "YES",
        "FEDERAL",
        "CANARA",
        "PNB",
        "UNION",
        "BOB",
        "IDFC",
        "AU",
        "RBL",
        "HSBC",
        "CITI",
        "DBS"
    )

    fun detect(sender: String, body: String): String? {

        val text = (sender + " " + body).uppercase()

        return banks.firstOrNull {
            text.contains(it)
        }

    }

}