package com.financeos.app

object YieldEngine {

    // A data blueprint defining how a card earns money
    data class CardProfile(
        val cardName: String,
        val baseYieldPercent: Double,
        val acceleratedCategories: Map<String, Double>
    )

    // The CFO's internal database of your wallet's rules
    private val walletProfiles = mapOf(
        "Axis Bank" to CardProfile(
            cardName = "Axis Bank",
            baseYieldPercent = 1.5, // 1.5% baseline
            acceleratedCategories = mapOf("Utilities" to 5.0, "Dining" to 4.0)
        ),
        "SBI" to CardProfile(
            cardName = "SBI Card",
            baseYieldPercent = 1.0,
            acceleratedCategories = mapOf("Dining" to 5.0, "Groceries" to 5.0)
        ),
        "Jupiter" to CardProfile(
            cardName = "Jupiter",
            baseYieldPercent = 1.0, // 1% Jewels
            acceleratedCategories = mapOf("Travel" to 2.0)
        ),
        "bobcard" to CardProfile(
            cardName = "bobcard",
            baseYieldPercent = 0.5,
            acceleratedCategories = mapOf("Groceries" to 2.5) // 5x points translating to 2.5% yield
        ),
        "Yes Bank" to CardProfile(
            cardName = "Yes Bank",
            baseYieldPercent = 1.0,
            acceleratedCategories = mapOf("Dining" to 3.0)
        )
    )

    /**
     * Calculates the exact Rupee value of the rewards earned on a transaction.
     */
    fun calculateRewardValue(amount: Double, category: String, paymentMethod: String): Double {
        // Find which card was used based on the SMS text
        var activeProfile: CardProfile? = null

        for ((key, profile) in walletProfiles) {
            if (paymentMethod.lowercase().contains(key.lowercase())) {
                activeProfile = profile
                break
            }
        }

        // If it's cash or an unknown card, the yield is ₹0.0
        if (activeProfile == null) return 0.0

        // Check if this category gets a bonus multiplier, otherwise use the base rate
        val yieldPercent = activeProfile.acceleratedCategories[category] ?: activeProfile.baseYieldPercent

        // Calculate the actual monetary reward (Amount * Percentage)
        return (amount * yieldPercent) / 100.0
    }
}