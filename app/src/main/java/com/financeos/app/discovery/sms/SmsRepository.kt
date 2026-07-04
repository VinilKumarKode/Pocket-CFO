package com.financeos.app.discovery.sms

import android.content.Context
import android.provider.Telephony
import com.financeos.app.models.FinancialMessage

class SmsRepository {

    fun getAllMessages(context: Context): List<FinancialMessage> {

        val messages = mutableListOf<FinancialMessage>()

        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            null,
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER
        )

        cursor?.use {

            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val senderIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            var count = 0

            while (it.moveToNext() && count < 100) {

                val body = it.getString(bodyIndex) ?: ""
                val sender = it.getString(senderIndex) ?: ""
                val date = it.getLong(dateIndex)

                messages.add(
                    FinancialMessage(
                        sender = sender,
                        body = body,
                        date = date
                    )
                )
                count++
            }

        }

        return messages

    }

}

