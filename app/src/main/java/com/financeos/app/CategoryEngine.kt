package com.financeos.app

object CategoryEngine {

    // The CFO's Dictionary of Expense Keywords
    private val categoryMap = mapOf(
        "Dining" to listOf("zomato", "swiggy", "itihaas", "veto bar", "starbucks", "cafe"),
        "Fuel" to listOf("hpcl", "bpcl", "indian oil", "reliance", "shell"),
        "Travel" to listOf("redbus", "zingbus", "irctc", "makemytrip", "uber", "ola"),
        "EMI/Loans" to listOf("tata capital", "hdfc bank", "bajaj", "muthoot"),
        "Utilities" to listOf("tsspdcl", "airtel", "jio", "act fibernet", "bescom"),
        "Groceries" to listOf("blinkit", "zepto", "instamart", "ratnadeep", "d-mart", "bigbasket")
    )

    /**
     * Scans the merchant name and returns the matched category.
     * Defaults to "General" if no match is found.
     */
    fun categorizeMerchant(merchantName: String): String {
        val lowerCaseMerchant = merchantName.lowercase()

        for ((category, keywords) in categoryMap) {
            for (keyword in keywords) {
                if (lowerCaseMerchant.contains(keyword)) {
                    return category
                }
            }
        }

        return "General" // Fallback for unknown merchants
    }
}