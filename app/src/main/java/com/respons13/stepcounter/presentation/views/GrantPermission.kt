package com.respons13.stepcounter.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn (ExperimentalPermissionsApi::class)
@Composable
        /**Componente */
fun GrantPermission (permission: PermissionState) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Precisa dar a permissão",
            modifier = Modifier.fillMaxWidth(), // Ocupa toda a largura do Column
            textAlign = androidx.compose.ui.text.style.TextAlign.Center  /** Centraliza o texto */
        )
        Button ( onClick= { permission.launchPermissionRequest() }   ) {
            Text("Permitir",
                style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
            )
        }
    }
}
