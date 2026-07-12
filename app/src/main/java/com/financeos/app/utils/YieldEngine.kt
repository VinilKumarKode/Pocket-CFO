package com.financeos.app.utils

import com.financeos.app.data.FinancialEntity

object YieldEngine {

    // A rulebook mapping popular Indian credit cards to their highest reward categories
    fun getBestCardForCategory(category: String, userCards: List<FinancialEntity>): String {
        val cardNames = userCards.map { it.name.lowercase() }

        return when (category) {
            "Food & Dining" -> {
                if (cardNames.any { it.contains("swiggy") }) "Use HDFC Swiggy (10% Cashback)"
                else if (cardNames.any { it.contains("sbi cashback") }) "Use SBI Cashback (5% Cashback)"
                else getDefaultCard(userCards)
            }
            "Bills & Utilities" -> {
                if (cardNames.any { it.contains("ace") || it.contains("axis ace") }) "Use Axis Ace (5% Cashback)"
                else if (cardNames.any { it.contains("amazon") }) "Use Amazon Pay ICICI (2% Cashback)"
                else getDefaultCard(userCards)
            }
            "Shopping" -> {
                if (cardNames.any { it.contains("sbi cashback") }) "Use SBI Cashback (5% Cashback)"
                else if (cardNames.any { it.contains("amazon") }) "Use Amazon Pay ICICI (5% on Amazon)"
                else if (cardNames.any { it.contains("flipkart") }) "Use Flipkart Axis (5% on Flipkart)"
                else getDefaultCard(userCards)
            }
            "Travel & Transport" -> {
                if (cardNames.any { it.contains("sbi cashback") }) "Use SBI Cashback (5% Cashback)"
                else getDefaultCard(userCards)
            }
            else -> getDefaultCard(userCards)
        }
    }

    private fun getDefaultCard(userCards: List<FinancialEntity>): String {
        if (userCards.isEmpty()) return "Use UPI or Debit Card (No Credit Cards Discovered)"

        // If no optimized card is found, default to the first credit card they own
        return "Use ${userCards.first().name} (Base Rewards)"
    }
}