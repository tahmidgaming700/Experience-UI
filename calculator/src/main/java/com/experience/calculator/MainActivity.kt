package com.experience.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import java.math.BigDecimal
import java.math.MathContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                ExperienceScaffold("Calculator", "Everyday arithmetic") {
                    var expression by rememberSaveable { mutableStateOf("") }
                    var result by rememberSaveable { mutableStateOf("0") }
                    val keys = listOf("7","8","9","÷","4","5","6","×","1","2","3","−","0",".","(",")","+","−","⌫","C")
                    OutlinedTextField(
                        value = expression,
                        onValueChange = { expression = it.filter { c -> c.isDigit() || c in "+-*/(). % " } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Expression") },
                        singleLine = true
                    )
                    Text(result, style = MaterialTheme.typography.displayMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        keys.chunked(4).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                row.forEach { key ->
                                    FilledTonalButton(
                                        onClick = {
                                            when (key) {
                                                "C" -> { expression = ""; result = "0" }
                                                "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                                "÷" -> expression += "/"
                                                "×" -> expression += "*"
                                                "−" -> expression += "-"
                                                else -> expression += key
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) { Text(key) }
                                }
                                repeat(4-row.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                    Button(onClick = {
                        result = try { formatNumber(ExpressionParser(expression).parse()) }
                        catch (e: Exception) { "Invalid expression" }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
                    Text("Supports brackets, decimals, +, −, ×, ÷ and %.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    if (!value.isFinite()) return "Result is not finite"
    return BigDecimal.valueOf(value).round(MathContext(12)).stripTrailingZeros().toPlainString()
}

private class ExpressionParser(private val input: String) {
    private var position = 0
    fun parse(): Double {
        val value = expression()
        skipSpaces()
        require(position == input.length) { "Unexpected input" }
        require(value.isFinite()) { "Non-finite result" }
        return value
    }
    private fun expression(): Double {
        var value = term()
        while (true) {
            skipSpaces()
            value = when {
                take('+') -> value + term()
                take('-') -> value - term()
                else -> return value
            }
        }
    }
    private fun term(): Double {
        var value = factor()
        while (true) {
            skipSpaces()
            value = when {
                take('*') -> value * factor()
                take('/') -> { val divisor = factor(); require(divisor != 0.0) { "Division by zero" }; value / divisor }
                take('%') -> { val divisor = factor(); require(divisor != 0.0) { "Division by zero" }; value % divisor }
                else -> return value
            }
        }
    }
    private fun factor(): Double {
        skipSpaces()
        if (take('+')) return factor()
        if (take('-')) return -factor()
        if (take('(')) {
            val value = expression()
            skipSpaces()
            require(take(')')) { "Missing closing bracket" }
            return value
        }
        val start = position
        var dots = 0
        while (position < input.length && (input[position].isDigit() || input[position] == '.')) {
            if (input[position] == '.') dots++
            require(dots <= 1) { "Invalid decimal" }
            position++
        }
        require(position > start && input.substring(start, position) != ".") { "Number expected" }
        return input.substring(start, position).toDouble()
    }
    private fun take(char: Char): Boolean {
        if (position < input.length && input[position] == char) { position++; return true }
        return false
    }
    private fun skipSpaces() { while (position < input.length && input[position].isWhitespace()) position++ }
}
