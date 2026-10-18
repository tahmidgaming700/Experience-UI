package com.experience.camera

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var status by remember { mutableStateOf("Ready") }
                ExperienceScaffold("Camera", "Capture with your installed camera") {
                    Button(onClick = {
                        try {
                            startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                            status = "Opened the system camera."
                        } catch (_: Exception) {
                            status = "No compatible camera app was found."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Open camera") }
                    Text(status)
                    Text("This starter delegates capture to the installed camera app. A native viewfinder needs CameraX integration and hardware testing.")
                }
            }
        }
    }
}
