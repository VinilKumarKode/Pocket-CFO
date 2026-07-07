package com.financeos.app

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object CampaignScraper {

    // A blueprint for a temporary offer
    data class ActiveCampaign(
        val cardName: String,
        val targetMerchant: String,
        val bonusYieldPercent: Double
    )

    // The CFO's list of URLs to check when you press the "Sync Offers" button
    private val bankOfferUrls = mapOf(
        "SBI Card" to "https://www.sbicard.com/en/personal/offers.page",
        "Axis Bank" to "https://www.axisbank.com/grab/deals/online-offers"
        // You can add more URLs here later without changing any code!
    )

    /**
     * Silently visits the websites in the background and hunts for deals.
     */
    suspend fun huntForActiveCampaigns(): List<ActiveCampaign> {
        val foundCampaigns = mutableListOf<ActiveCampaign>()

        // We use 'Dispatchers.IO' so this heavy internet task doesn't freeze your phone screen
        withContext(Dispatchers.IO) {
            for ((cardName, url) in bankOfferUrls) {
                try {
                    // The "Research Assistant" visits the webpage
                    val document = Jsoup.connect(url).get()

                    // Extracts all the raw, readable text from the entire page
                    val pageText = document.text().lowercase()

                    // CFO Intelligence: Look for standard offer formats
                    // Example: If the page mentions "amazon" and "10% cashback"
                    if (pageText.contains("amazon") && pageText.contains("10%")) {
                        foundCampaigns.add(ActiveCampaign(cardName, "Amazon", 10.0))
                    }
                    if (pageText.contains("zomato") && pageText.contains("20%")) {
                        foundCampaigns.add(ActiveCampaign(cardName, "Zomato", 20.0))
                    }

                    Log.d("PocketCFO", "Scraped $cardName successfully. Found ${foundCampaigns.size} deals.")

                } catch (e: Exception) {
                    Log.e("PocketCFO", "Failed to scrape $cardName: ${e.message}")
                }
            }
        }
        return foundCampaigns
    }
}