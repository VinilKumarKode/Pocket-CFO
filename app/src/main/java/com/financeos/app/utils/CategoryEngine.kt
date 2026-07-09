package com.financeos.app.utils

object CategoryEngine {

    fun getCategoryForMerchant(merchantName: String): String {
        val name = merchantName.lowercase()

        return when {
            // Food & Dining
            name.contains("zomato") || name.contains("swiggy") ||
                    name.contains("mehfil") || name.contains("kfc") ||
                    name.contains("mcdonalds") || name.contains("starbucks") -> "Food & Dining"

            // Travel & Commute
            name.contains("irctc") || name.contains("confirmtkt") ||
                    name.contains("uber") || name.contains("ola") ||
                    name.contains("makemytrip") || name.contains("indigo") -> "Travel & Commute"

            // Groceries & Daily Needs
            name.contains("blinkit") || name.contains("zepto") ||
                    name.contains("instamart") || name.contains("dmart") ||
                    name.contains("bigbasket") -> "Groceries"

            // Shopping & E-Commerce
            name.contains("amazon") || name.contains("flipkart") ||
                    name.contains("myntra") || name.contains("ajio") -> "Shopping"

            // Bills & Utilities
            name.contains("jio") || name.contains("airtel") ||
                    name.contains("bescom") || name.contains("tsspdcl") -> "Bills & Utilities"

            // Health & Pharmacy
            name.contains("apollo") || name.contains("pharmeasy") ||
                    name.contains("netmeds") -> "Health & Medical"

            // Entertainment
            name.contains("netflix") || name.contains("prime") ||
                    name.contains("bookmyshow") || name.contains("spotify") -> "Entertainment"

            // Fallback for anything else
            else -> "Other Expenses"
        }
    }
}