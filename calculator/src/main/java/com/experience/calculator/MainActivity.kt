package com.experience.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var expression by rememberSaveable { mutableStateOf("") }
                var result by rememberSaveable { mutableStateOf("0") }
                var error by rememberSaveable { mutableStateOf(false) }
                fun append(value: String) {
                    expression += value
                    error = false
                }
                fun evaluate() {
                    runCatching { ExpressionParser(expression).parse() }
                        .onSuccess { result = formatNumber(it); error = false }
                        .onFailure { result = it.message ?: "Invalid expression"; error = true }
                }
                ExperienceScaffold("Calculator", "A clear, capable everyday calculator") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("EXPRESSION", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = expression,
                                onValueChange = { value ->
                                    expression = value.filter { it.isDigit() || it in "+-*/().% " }
                                    error = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. (12 + 8) / 2") },
                                singleLine = false,
                                textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.End),
                                supportingText = { Text("Brackets, decimals and operator precedence supported") }
                            )
                            Text(
                                result,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.End,
                                color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    val rows = listOf(
                        listOf("C", "(", ")", "⌫"),
                        listOf("7", "8", "9", "÷"),
                        listOf("4", "5", "6", "×"),
                        listOf("1", "2", "3", "−"),
                        listOf("%", "0", ".", "+")
                    )
                    rows.forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { key ->
                                val isOperator = key in listOf("÷", "×", "−", "+", "%")
                                val action: () -> Unit = {
                                    when (key) {
                                        "C" -> { expression = ""; result = "0"; error = false }
                                        "⌫" -> { if (expression.isNotEmpty()) expression = expression.dropLast(1); error = false }
                                        else -> append(when (key) { "÷" -> "/"; "×" -> "*"; "−" -> "-"; else -> key })
                                    }
                                }
                                if (key == "C") {
                                    OutlinedButton(onClick = action, modifier = Modifier.weight(1f).heightIn(min = 52.dp)) { Text(key) }
                                } else {
                                    Button(
                                        onClick = action,
                                        modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isOperator) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isOperator) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) { Text(key, fontSize = 20.sp) }
                                }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { expression = ""; result = "0"; error = false }, modifier = Modifier.weight(1f).heightIn(min = 54.dp)) { Text("Clear") }
                        Button(onClick = ::evaluate, modifier = Modifier.weight(2f).heightIn(min = 54.dp)) { Text("＝  Calculate", fontSize = 17.sp) }
                    }
                    Text("Calculations run locally on your device.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    require(value.isFinite()) { "Result is out of range" }
    if (value == 0.0) return "0"
    return BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
}

/** Recursive-descent parser: user input is parsed as arithmetic, never executed as code. */
private class ExpressionParser(private val source: String) {
    private var position = 0

    fun parse(): Double {
        require(source.isNotBlank()) { "Enter an expression" }
        val value = expression()
        skipSpaces()
        require(position == source.length) { "Check the expression near '${source.drop(position).take(8)}'" }
        require(value.isFinite()) { "Result is out of range" }
        return value
    }

    private fun expression(): Double {
        var value = term()
        while (true) {
            skipSpaces()
            value = when {
                consume('+') -> value + term()
                consume('-') -> value - term()
                else -> return value
            }
        }
    }

    private fun term(): Double {
        var value = unary()
        while (true) {
            skipSpaces()
            value = when {
                consume('*') -> value * unary()
                consume('/') -> {
                    val divisor = unary()
                    require(divisor != 0.0) { "Cannot divide by zero" }
                    value / divisor
                }
                else -> return value
            }
        }
    }

    private fun unary(): Double {
        skipSpaces()
        return when {
            consume('+') -> unary()
            consume('-') -> -unary()
            else -> percentageOrPrimary()
        }
    }

    private fun percentageOrPrimary(): Double {
        var value = primary()
        skipSpaces()
        if (consume('%')) value /= 100.0
        return value
    }

    private fun primary(): Double {
        skipSpaces()
        if (consume('(')) {
            val value = expression()
            skipSpaces()
            require(consume(')')) { "Missing closing bracket" }
            return value
        }
        val start = position
        var dots = 0
        while (position < source.length && (source[position].isDigit() || source[position] == '.')) {
            if (source[position] == '.') dots++
            position++
        }
        require(position > start && dots <= 1) { "Enter a valid number" }
        return source.substring(start, position).toDoubleOrNull() ?: error("Enter a valid number")
    }

    private fun consume(char: Char): Boolean {
        if (position < source.length && source[position] == char) {
            position++
            return true
        }
        return false
    }

    private fun skipSpaces() {
        while (position < source.length && source[position].isWhitespace()) position++
    }
}
