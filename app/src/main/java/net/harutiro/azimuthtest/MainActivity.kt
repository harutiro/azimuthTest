package net.harutiro.azimuthtest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.harutiro.azimuthtest.ui.theme.AzimuthTestTheme


class MainActivity : ComponentActivity(), SensorEventListener {


    val TAG: String = "SensorTest2"
    val RAD2DEG: Double = 180 / Math.PI

    var sensorManager: SensorManager? = null

    var rotationMatrix: FloatArray = FloatArray(9)
    var gravity: FloatArray = FloatArray(3)
    var geomagnetic: FloatArray = FloatArray(3)
    var attitude: FloatArray = FloatArray(3)

    var azimuthText = mutableStateOf("")
    var pitchText = mutableStateOf("")
    var rollText = mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initSensor()

        setContent {
            AzimuthTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(
                            text = "Azimuth",
                            modifier = Modifier.padding(innerPadding)
                        )
                        Text(
                            text = azimuthText.value,
                            modifier = Modifier.padding(innerPadding)
                        )

                        Text(
                            text = "Pitch",
                            modifier = Modifier.padding(innerPadding)
                        )
                        Text(
                            text = pitchText.value,
                            modifier = Modifier.padding(innerPadding)
                        )
                        Text(
                            text = "Roll",
                            modifier = Modifier.padding(innerPadding)
                        )
                        Text(
                            text = rollText.value,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    public override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    protected fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.getType()) {
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values.clone()
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values.clone()
        }

        if (geomagnetic != null && gravity != null) {
            SensorManager.getRotationMatrix(
                rotationMatrix, null,
                gravity, geomagnetic
            )

            SensorManager.getOrientation(
                rotationMatrix,
                attitude
            )

            azimuthText.value = (attitude[0] * RAD2DEG ).toString()
            pitchText.value = (attitude[1] * RAD2DEG).toString()
            rollText.value = (attitude[2] * RAD2DEG).toString()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AzimuthTestTheme {
    }
}