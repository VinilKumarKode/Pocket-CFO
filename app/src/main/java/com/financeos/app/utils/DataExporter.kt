package com.financeos.app.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.financeos.app.data.Transaction
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExporter {

    fun exportDatabaseToCSV(context: Context, transactions: List<Transaction>) {
        try {
            // 1. Create a temporary file in the phone's cache
            val fileName = "PocketCFO_Export_${System.currentTimeMillis()}.csv"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, fileName)
            val writer = FileWriter(file)

            // 2. Write the Excel Column Headers
            writer.append("Date,Type,Category,Merchant/Description,Amount (INR),Payment Method,Raw SMS\n")

            // 3. Loop through your database and write each row
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            for (t in transactions) {
                val dateString = dateFormat.format(Date(t.date))
                // We wrap text in quotes just in case there are commas in the description
                writer.append("$dateString,${t.type},\"${t.category}\",\"${t.description}\",${t.amount},\"${t.paymentMethod}\",\"${t.rawMessage}\"\n")
            }

            writer.flush()
            writer.close()

            // 4. Create the secure bridge to share the file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            // 5. Launch the Android Share Menu
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "PocketCFO Data Export")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share financial data via..."))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}