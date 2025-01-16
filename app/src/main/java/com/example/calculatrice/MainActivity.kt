package com.example.calculatrice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var tvResult: TextView
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false
    private var currentExpression: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                appendDigit((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { appendOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { appendOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { appendOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { appendOperator("/") }
        findViewById<Button>(R.id.btnEqual).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
    }

    private fun appendDigit(digit: String) {
        currentExpression += digit
        tvResult.text = currentExpression
        lastNumeric = true
    }

    private fun appendOperator(operator: String) {
        if (lastNumeric && !currentExpression.endsWith(operator)) {
            currentExpression += operator
            tvResult.text = currentExpression
            lastNumeric = false
            lastDot = false
        }
    }

    private fun calculateResult() {
        try {
            val result = eval(currentExpression)
            tvResult.text = result.toString()
            currentExpression = result.toString()
        } catch (e: Exception) {
            tvResult.text = "Erreur"
        }
    }

    private fun clear() {
        tvResult.text = "0"
        currentExpression = ""
        lastNumeric = false
        lastDot = false
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            fun parse(): Double {
                val result = term()
                if (currentIndex < expression.length) throw RuntimeException("Unexpected character")
                return result
            }

            var currentIndex = 0

            fun term(): Double {
                var result = factor()
                while (currentIndex < expression.length) {
                    when (expression[currentIndex]) {
                        '+' -> {
                            currentIndex++
                            result += factor()
                        }
                        '-' -> {
                            currentIndex++
                            result -= factor()
                        }
                        else -> return result
                    }
                }
                return result
            }

            fun factor(): Double {
                var result = if (expression[currentIndex] == '(') {
                    currentIndex++
                    val subExpression = term()
                    currentIndex++
                    subExpression
                } else {
                    number()
                }
                while (currentIndex < expression.length) {
                    when (expression[currentIndex]) {
                        '*' -> {
                            currentIndex++
                            result *= number()
                        }
                        '/' -> {
                            currentIndex++
                            result /= number()
                        }
                        else -> return result
                    }
                }
                return result
            }

            fun number(): Double {
                val start = currentIndex
                while (currentIndex < expression.length && (expression[currentIndex].isDigit() || expression[currentIndex] == '.')) {
                    currentIndex++
                }
                return expression.substring(start, currentIndex).toDouble()
            }
        }.parse()
    }
}
