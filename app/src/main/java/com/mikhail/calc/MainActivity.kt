package com.mikhail.calc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.MathContext


/*
* TODO:
* 1. add fractals
* 2. R doesn't work correctly
* 3. Add -x
* */

class MainActivity : AppCompatActivity() {
    private val TAG: String = "Calculator"
    data class BracketPositions(val openPos: Int, val closePos: Int, val bc: Boolean)
    var fullExpression: StringBuilder = StringBuilder()
    var resultPreview: StringBuilder = StringBuilder()
    var prescision: MathContext = MathContext(5)        /*Need to add key for this option. By default 5*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun onClick(v: View) {
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
            else -> ""
        }
        fullExpression.append(value)

        when (v.id) {
            R.id.btnClear -> {
                fullExpression.clear()
                resultPreview.clear()
            }
            R.id.btnRemoveSymbol -> {
                if(fullExpression.length > 1) {
                    fullExpression.deleteCharAt(fullExpression.lastIndex)
                } else {
                    fullExpression.clear()
                    resultPreview.clear()
                }
            }
            R.id.btnEquals -> {
                fullExpression = resultPreview
            }
        }

        if(isCorrect()) {
            parseExpression()
        }
        updateCalcWindow()
    }

    private fun isCorrect(): Boolean {
        var i = fullExpression.takeLast(1).toString()
        if (i == "/" || i == "*" || i == "-" || i == "+" || !isBracketsCorrect()) {
            Log.d(TAG, "Expression $fullExpression isn't correct")
            return false
        }   //not finished expression
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
        Log.d(TAG, "parseExpression()")
        while (resultPreview.contains("\\*|\\-|\\+|\\/".toRegex())) {

            var bracketPair: BracketPositions = getDeepestBrackets()
            if(!bracketPair.bc) {
                Log.d(TAG, "Number of '(' != ')'")
                return
            }
            if (bracketPair.closePos == 0) {
                resultPreview = calculate(resultPreview.toString())
                break;
            } else {
                val o = bracketPair.openPos + 1
                val c = bracketPair.closePos
                resultPreview.replace(o-1, c+1, calculate(resultPreview.substring(o, c)).toString())
            }
        }
        updatePreview(resultPreview.toString())
    }

    private fun calculate(str: String):StringBuilder {
        Log.d(TAG, "calculate expression: $str")
        var bd = BigDecimal(0)
        var valuesList: MutableList<String> = str.split("\\*|\\-|\\+|\\/".toRegex()).toMutableList()
        var signsList = StringBuilder(str.replace("\\d|\\.".toRegex(), ""))
        var signMult = signsList.indexOf('*')
        var signDiv = signsList.indexOf('/')
        while (signMult != -1 || signDiv != -1) {
            if (signMult != -1 && signDiv != -1) {
                if (signMult < signDiv) {
                    bd = valuesList[signMult].toBigDecimal().multiply(valuesList[signMult + 1].toBigDecimal(), prescision)
                    valuesList.removeAt(signMult + 1)
                    valuesList.removeAt(signMult)
                    signsList.deleteCharAt(signMult)
                    valuesList.add(signMult, bd.toString())
                } else {
                    bd = valuesList[signDiv].toBigDecimal().divide(valuesList[signDiv + 1].toBigDecimal(), prescision)
                    valuesList.removeAt(signDiv + 1)
                    valuesList.removeAt(signDiv)
                    signsList.deleteCharAt(signDiv)
                    valuesList.add(signDiv, bd.toString())
                }
            } else if (signDiv != -1) {
                bd = valuesList[signDiv].toBigDecimal().divide(valuesList[signDiv + 1].toBigDecimal(), prescision)
                valuesList.removeAt(signDiv + 1)
                valuesList.removeAt(signDiv)
                signsList.deleteCharAt(signDiv)
                valuesList.add(signDiv, bd.toString())
            } else if (signMult != -1) {
                bd = valuesList[signMult].toBigDecimal().multiply(valuesList[signMult + 1].toBigDecimal(), prescision)
                valuesList.removeAt(signMult + 1)
                valuesList.removeAt(signMult)
                signsList.deleteCharAt(signMult)
                valuesList.add(signMult, bd.toString())
            }
            signMult = signsList.indexOf('*')
            signDiv = signsList.indexOf('/')
        }

        var signPlus = signsList.indexOf('+')
        var signMinus = signsList.indexOf('-')
        while (signPlus != -1 || signMinus != -1) {
            if (signPlus != -1 && signMinus != -1) {
                if (signPlus > signMinus) {
                    bd = valuesList[signMinus].toBigDecimal().subtract(valuesList[signMinus + 1].toBigDecimal(), prescision)
                    valuesList.removeAt(signMinus + 1)
                    valuesList.removeAt(signMinus)
                    signsList.deleteCharAt(signMinus)
                    valuesList.add(signMinus, bd.toString())
                } else {
                    bd = valuesList[signPlus].toBigDecimal().add(valuesList[signPlus + 1].toBigDecimal(), prescision)
                    valuesList.removeAt(signPlus + 1)
                    valuesList.removeAt(signPlus)
                    signsList.deleteCharAt(signPlus)
                    valuesList.add(signPlus, bd.toString())
                }
            } else if(signPlus != -1) {
                bd = valuesList[signPlus].toBigDecimal().add(valuesList[signPlus + 1].toBigDecimal(), prescision)
                valuesList.removeAt(signPlus + 1)
                valuesList.removeAt(signPlus)
                signsList.deleteCharAt(signPlus)
                valuesList.add(signPlus, bd.toString())
            } else if(signMinus != -1) {
                bd = valuesList[signMinus].toBigDecimal().subtract(valuesList[signMinus + 1].toBigDecimal(), prescision)
                valuesList.removeAt(signMinus + 1)
                valuesList.removeAt(signMinus)
                signsList.deleteCharAt(signMinus)
                valuesList.add(signMinus, bd.toString())
            }
            signPlus = signsList.indexOf('+')
            signMinus = signsList.indexOf('-')
        }

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
