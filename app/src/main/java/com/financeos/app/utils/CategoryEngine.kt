package com.financeos.app.utils

object CategoryEngine {

    fun getCategoryForMerchant(merchantName: String): String {
        // Convert to lowercase so we don't have to worry about capital letters
        val name = merchantName.lowercase()

        return when {
            // Food, Dining & Groceries
            name.contains("zomato") || name.contains("swiggy") || name.contains("blinkit") ||
                    name.contains("zepto") || name.contains("instamart") || name.contains("mcdonald") ||
                    name.contains("kfc") || name.contains("starbucks") || name.contains("grocery") ||
                    name.contains("ratnadeep") -> "Food & Dining"

            // Transport & Travel
            name.contains("uber") || name.contains("ola") || name.contains("rapido") ||
                    name.contains("irctc") || name.contains("makemytrip") || name.contains("indigo") ||
                    name.contains("metro") -> "Travel & Transport"

            // Shopping & E-Commerce
            name.contains("amazon") || name.contains("amzn") || name.contains("flipkart") ||
                    name.contains("myntra") || name.contains("ajio") || name.contains("reliance") ||
                    name.contains("dmart") || name.contains("lifestyle") -> "Shopping"

            // Utilities & Bills
            name.contains("airtel") || name.contains("jio") || name.contains("vi") ||
                    name.contains("bescom") || name.contains("tsspdcl") || name.contains("electricity") ||
                    name.contains("recharge") || name.contains("broadband") -> "Bills & Utilities"

            // Entertainment
            name.contains("netflix") || name.contains("prime") || name.contains("hotstar") ||
                    name.contains("spotify") || name.contains("bookmyshow") || name.contains("pvr") -> "Entertainment"

            // Finance, EMI & UPI generic gateways
            name.contains("cred") || name.contains("paytm") || name.contains("phonepe") ||
                    name.contains("gpay") || name.contains("upi") || name.contains("razorpay") ||
                    name.contains("billdesk") -> "Digital Payments (Uncategorized)"

            // Default Fallback
            else -> "Other Expenses"
        }
    }
}