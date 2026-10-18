package com.experience.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                
                
                
                ExperienceScaffold("Calculator", "Quick arithmetic") {
                    var expression by rememberSaveable { mutableStateOf("") }
                var result by rememberSaveable { mutableStateOf("0") }
                OutlinedTextField(value = expression, onValueChange = { expression = it.filter { ch -> ch.isDigit() || ch in "+-*/(). " } }, modifier = Modifier.fillMaxWidth(), label = { Text("Expression") }, singleLine = true)
                Text(result, style = MaterialTheme.typography.displayMedium)
                Button(onClick = { result = calculateBasic(expression) }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
                OutlinedButton(onClick = { expression = ""; result = "0" }, modifier = Modifier.fillMaxWidth()) { Text("Clear") }
                Text("Supports basic two-number arithmetic (+, -, *, /). Complex expressions are not evaluated.")
                }
            }
        }
    }
}

private fun calculateBasic(input: String): String {
    val match = Regex("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+*/-])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$").matchEntire(input)
        ?: return "Enter: number operator number"
    val left = match.groupValues[1].toDouble()
    val op = match.groupValues[2]
    val right = match.groupValues[3].toDouble()
    val value = when (op) { "+" -> left + right; "-" -> left - right; "*" -> left * right; "/" -> if (right == 0.0) return "Cannot divide by zero" else left / right; else -> return "Invalid operation" }
    return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
}
