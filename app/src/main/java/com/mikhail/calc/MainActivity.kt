package com.mikhail.calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.MathContext

/**
 * TODO:
 * replace EditText with something else what
 *
 * */


class MainActivity : AppCompatActivity() {
    private val TAG: String = "Calculator"
    data class BracketPositions(val openPos: Int, val closePos: Int, val bc: Boolean)
    private var fullExpression = StringBuilder()
    private var resultPreview = StringBuilder()
    private var precision: MathContext = MathContext(5)        /*Need to add key for this option. By default 5*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        Log.d(TAG, "onClick(${v})")
        val value: String = when (v.id) {
            R.id.btn0 -> "0"
            R.id.btn1 -> "1"
            R.id.btn2 -> "2"
            R.id.btn3 -> "3"
            R.id.btn4 -> "4"
            R.id.btn5 -> "5"
            R.id.btn6 -> "6"
            R.id.btn7 -> "7"
            R.id.btn8 -> "8"
            R.id.btn9 -> "9"
            R.id.btnDot -> "."
            R.id.btnDivide -> "/"
            R.id.btnMultiply -> "*"
            R.id.btnPlus -> "+"
            R.id.btnSubtract -> "-"
            R.id.btnOpenBrace -> "("
            R.id.btnCloseBrace -> ")"
            R.id.btnPersent -> "%"
            else -> ""
        }
        fullExpression.append(value)

        when (v.id) {
            R.id.btnClear -> {
                fullExpression.clear()
            }
            R.id.btnRemoveSymbol -> {
                if(fullExpression.isNotEmpty()) {
                    fullExpression.deleteCharAt(fullExpression.lastIndex)
                }
            }
            R.id.btnEquals -> {
                fullExpression = resultPreview
            }
        }

        updateCalcWindow()
        if(isCorrect()) {
            try {
                parseExpression()
                Log.d(TAG, "Full expression after parsing: $fullExpression")
            } catch (exc: Exception) {
                Log.d(TAG, "incorrect expression")
                resultPreview = StringBuilder("incorrect expression")
            }
        }
    }

    private fun isCorrect(): Boolean {
        val i = fullExpression.takeLast(1).toString()
        if (i == "/" || i == "*" || i == "-" || i == "+" || !isBracketsCorrect()) {
            Log.d(TAG, "Expression $fullExpression isn't correct")
            return false
        }
        Log.d(TAG, "Expression $fullExpression is correct")
        resultPreview = fullExpression
        return true
    }

    private fun updatePreview(text: String) {
        tvResultPreview.text = text
    }

    private fun updateCalcWindow() {
        etCalculationWindow.setText(fullExpression.toString())
    }

    private fun parseExpression() {
        Log.d(TAG, "parseExpression($resultPreview)")
        while (resultPreview.contains("[*\\-+/]".toRegex())) {
            val bracketPair: BracketPositions = getDeepestBrackets()
            if(!bracketPair.bc) {
                Log.d(TAG, "Number of '(' != ')'")
                return
            }
            if (bracketPair.closePos == 0) {
                resultPreview = calculate(resultPreview.toString())
                break
            } else {
                val o = bracketPair.openPos + 1
                val c = bracketPair.closePos
                resultPreview.replace(o-1, c+1, calculate(resultPreview.substring(o, c)).toString())
            }
        }
        updatePreview(resultPreview.toString())
    }

    private fun calculate(str: String):StringBuilder {
        val string = StringBuilder(str)
        Log.d(TAG, "calculate expression: $string")
        for((i, s) in string.withIndex()) {
            if(s == '-') {
                if (i > 0) {
                    if (string[i-1].isDigit()) {
                        string.insert(i,'+')
                    }
                }
            }
        }
        Log.d(TAG, "calculate expression: $string")
        val values = string.split("[+|*/]".toRegex()).toMutableList()
        val signs = StringBuilder(string.replace("\\d|\\.|-|%".toRegex(), ""))
        var multPos = signs.indexOf('*')
        var divPos = signs.indexOf('/')
        var sign = 0
        var bd = BigDecimal(values[0])
        Log.d(TAG, "Point")
        for((i, s) in values.withIndex()) {
            if(s.last() == '%') {
                var sb = StringBuilder(values[i])
                values[i] = sb.deleteCharAt(sb.lastIndex).toString()
                var multiplier = values[i-1].toBigDecimal().divide(BigDecimal(100))
                values[i] = values[i].toBigDecimal().multiply(multiplier).toString()
            }
        }

        Log.d(TAG, "values: ${values.toString()}")
        Log.d(TAG, "Signs: $signs")
        while (multPos != -1 || divPos != -1) {
            if (divPos != -1 && (divPos < multPos || multPos == -1)) {
                sign = divPos
                bd = values[divPos].toBigDecimal().divide(values[sign + 1].toBigDecimal(), precision)
            } else if (multPos != -1 && (multPos < divPos || divPos == -1)) {
                sign = multPos
                bd = values[multPos].toBigDecimal().multiply(values[sign + 1].toBigDecimal(), precision)
            } else { Log.d(TAG, "WTF?") }
            values.removeAt(sign + 1)
            values.removeAt(sign)
            signs.deleteCharAt(sign)
            values.add(sign, bd.toString())
            multPos = signs.indexOf('*')
            divPos = signs.indexOf('/')
        }

        var addPos = signs.indexOf('+')
        while (addPos != -1) {
            sign = addPos
            bd = values[sign].toBigDecimal().add(values[sign + 1].toBigDecimal(), precision)
            values.removeAt(sign + 1)
            values.removeAt(sign)
            signs.deleteCharAt(sign)
            values.add(sign, bd.toString())
            addPos = signs.indexOf('+')
        }
        Log.d(TAG, "bd = $bd")
        return StringBuilder(bd.toString())
    }

    private fun getDeepestBrackets(): BracketPositions {
        var depth = 0
        var maxDepthStart = 0
        var maxDepthEnd = 0
        var depthMax = 0
        for ((index, i) in resultPreview.withIndex()) {
            if (i == '(') {
                depth++
                if (depth > depthMax) {
                    maxDepthStart = index
                    depthMax = depth
                }
            } else if (i == ')') {
                if (depth == depthMax) {
                    maxDepthEnd = index
                }
                depth--
            }
        }
        if(maxDepthEnd > 0) {
            Log.d(TAG, "bracket at $maxDepthStart and $maxDepthEnd")
        } else {
            Log.d(TAG, "No brackets in $resultPreview expression")
        }
        return BracketPositions(maxDepthStart, maxDepthEnd, isBracketsCorrect())
    }

    private fun isBracketsCorrect(): Boolean {
        var bracketNum = 0
        for(i in fullExpression) {
            if(i == '(') {
                bracketNum++
            }
            if(i == ')') {
                bracketNum--
            }
        }
        if(bracketNum == 0) {
            return true
        }
        return false
    }
}
