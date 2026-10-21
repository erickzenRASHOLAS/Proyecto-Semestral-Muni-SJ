package com.duoc.securitysjapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.duoc.securitysjapp.ui.theme.SecuritySJAppTheme
import com.duoc.securitysjapp.ui.theme.Formulario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecuritySJAppTheme {
                var texto by remember { mutableStateOf("") }
                Text(
                    text = "HOla------------"

                )
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto=it},
                    label = { Text("Ingresa nombre") },
                    )
            }
        }
    }
}

