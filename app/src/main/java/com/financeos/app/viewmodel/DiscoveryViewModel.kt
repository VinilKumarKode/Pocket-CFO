package com.financeos.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DiscoveryViewModel {

    var status by mutableStateOf("Not Started")
        private set

    fun updateStatus(newStatus: String) {

        status = newStatus

    }

}

