/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.respons13.stepcounter.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.respons13.stepcounter.presentation.theme.StepCounterTheme
import com.respons13.stepcounter.presentation.views.GrantPermission
import com.respons13.stepcounter.presentation.views.HeartLabel
import com.respons13.stepcounter.presentation.views.NotSupported
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var healthServicesClient: HealthServicesClient
    private lateinit var measureClient: MeasureClient

    private val supportState = mutableStateOf(false)

    private val availabilityState = mutableStateOf(DataTypeAvailability.UNKNOWN)
    private val heartRateState = mutableDoubleStateOf(0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        healthServicesClient = HealthServices.getClient(this)
        measureClient = healthServicesClient.measureClient

        setTheme(android.R.style.Theme_DeviceDefault)

        lifecycleScope.launch{
            supportState.value = checkSensorAvailability()

            setMeasureCallback().collect {data ->
                when (data) {
                    is DataTypeAvailability -> availabilityState.value = data
                    is Double -> heartRateState.doubleValue = data
                }
            }
        }

        setContent {
            WearApp(supportState, availabilityState, heartRateState)
        }
    }

    private suspend fun checkSensorAvailability(): Boolean{
        val capabilities = measureClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure)
    }

    private fun setMeasureCallback() = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                if (availability is DataTypeAvailability)
                    trySendBlocking (availability)
            }
            override fun onDataReceived(data: DataPointContainer) {
                val hrBpm = data.getData(DataType.HEART_RATE_BPM)
                if (hrBpm.isNotEmpty())
                    trySendBlocking (hrBpm.last().value)
            }
        }

        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose{
            runBlocking{
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                    .await() /* Adicionei .await() por minha conta, pois não vi no código da professora e resolvi seguir o site https://medium.com/@fierydinesh/heart-rate-in-wearos-health-services-android-97dadc0c2987  */
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WearApp(supportState: State<Boolean>,
            availabilityState: State<DataTypeAvailability>,
            heartRateState: State<Double>) {
    val permission = rememberPermissionState(android.Manifest.permission.BODY_SENSORS)

    val support by remember { supportState }

    StepCounterTheme {
        Scaffold (modifier = Modifier.fillMaxSize(), timeText = { TimeText() }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background (MaterialTheme.colors.background)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                /** Com a linha abaixo, o texto fica centralizado horizontalmente*/
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    /** Com as 3 linhas abaixo, o texto ‘Tenho Permissão’ fica centralizado horizontalmente*/
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(support) {
                        if (permission.status == PermissionStatus.Granted){
                            HeartLabel(availabilityState, heartRateState )
                        }
                        else{  GrantPermission(permission)   }
                    }
                    else{
                        NotSupported()
                    }
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(mutableStateOf(true), remember { mutableStateOf(DataTypeAvailability.AVAILABLE) }, remember { mutableDoubleStateOf(75.0) })
}