package com.respons13.stepcounter.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text

@Composable
        /**Componente */
fun NotSupported() {
    Column {
        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Yellow)
        Text("Sensor não está disponível")
    }
}
