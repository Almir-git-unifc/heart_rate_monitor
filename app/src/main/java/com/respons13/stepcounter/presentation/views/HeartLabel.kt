package com.respons13.stepcounter.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import kotlin.math.roundToInt

@Composable
fun HeartLabel(availabilityState: State<DataTypeAvailability> ,
               heartRateState: State<Double>
) {

    val availability by remember { availabilityState  }
    val heartRate by remember { heartRateState }

    val icon = when (availability) {
        DataTypeAvailability.AVAILABLE  -> Icons.Filled.Favorite
        DataTypeAvailability.ACQUIRING -> Icons.Filled.FavoriteBorder
        DataTypeAvailability.UNAVAILABLE,
        DataTypeAvailability.UNAVAILABLE_DEVICE_OFF_BODY ->
            Icons.Filled.HeartBroken
        else -> Icons.Filled.QuestionMark
    }

    val text = when (availability) {
        DataTypeAvailability.AVAILABLE  -> "${heartRate.roundToInt()} bpm"
        else ->  "- -"
    }


    Row {
        Icon (imageVector = icon, tint = Color.Red, contentDescription = null)
        Text(text) // TODO: add state
    }
}
