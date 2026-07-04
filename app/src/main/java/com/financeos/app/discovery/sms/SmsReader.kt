package com.financeos.app.discovery.sms

import android.content.Context
import com.financeos.app.models.FinancialMessage

class SmsReader(
    private val repository: SmsRepository = SmsRepository()
) {

    fun readMessages(context: Context): List<FinancialMessage> {

        return repository.getAllMessages(context)

    }

}
