package com.mikhail.calc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Script
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.time.temporal.ValueRange
import kotlin.math.roundToInt
import kotlin.math.sign

class MainActivity : AppCompatActivity() {
    private val TAG: String = "TEST_LOG"
    data class BracketPositions(val openPos: Int, val closePos: Int, val bc: Int)
    var fullExpression: StringBuilder = StringBuilder()
    var resultPreview: StringBuilder = StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun onNumberClick(v: View) {
        val value: String = when (v.id) {
            R.id.btn0-> "0"
            R.id.btn1-> "1"
            R.id.btn2-> "2"
            R.id.btn3-> "3"
            R.id.btn4-> "4"
            R.id.btn5-> "5"
            R.id.btn6-> "6"
            R.id.btn7-> "7"
            R.id.btn8-> "8"
            R.id.btn9-> "9"
            R.id.btnDot-> "."
            R.id.btnDivide-> "/"
            R.id.btnMultiply-> "*"
            R.id.btnPlus-> "+"
            R.id.btnSubtract-> "-"
            R.id.btnOpenBrace-> "("
            R.id.btnCloseBrace-> ")"
            else-> ""
        }
        fullExpression.append(value)
        prepare_calculations()
    }
    public fun onSpecialKey(v: View) {
        when(v.id) {
            R.id.btnClear -> {
                fullExpression.clear()
                resultPreview.clear()
                updatePreview()
            }
            R.id.btnRemoveSymbol -> {
                fullExpression.deleteCharAt(fullExpression.lastIndex)
                resultPreview.deleteCharAt(fullExpression.lastIndex)
                prepare_calculations()
            }
            R.id.btnEquals -> {
                fullExpression = resultPreview
                updatePreview()
            }
        }
    }

    private fun updatePreview() {
        Log.d(TAG, "updatePreview with $resultPreview")
        etCalculationWindow.setText(fullExpression)
        tvResultPreview.text = resultPreview
        var i = fullExpression.takeLast(1).toString()
        if (i == "/" || i == "*" || i == "-" || i == "+") return    //not finished expression

       // var resultPrevRound = resultPreview.toString().toDouble()
       // if (resultPrevRound % resultPreview.toString().toInt() == 0.0) {
       //     Log.d(TAG, "Whole number")
       //     tvResultPreview.text = resultPreview.toString().toInt().toString()
       // } else {
       //     Log.d(TAG, "Fractional number")
       //     tvResultPreview.text = resultPreview
       // }
    }

    public fun prepare_calculations() {
        resultPreview = fullExpression
        updatePreview()
        var i = fullExpression.takeLast(1).toString()
        if (i == "/" || i == "*" || i == "-" || i == "+") return    //not finished expression

        while (resultPreview.contains("\\*|\\-|\\+|\\/".toRegex())) {

            var bracketPair = getDeepestBrackets()
            if(bracketPair.bc != 0) return

            println("bracket at ${bracketPair.openPos} and ${bracketPair.closePos}")
            if (bracketPair.closePos == 0) { /*no brackets*/
                resultPreview = calculate(resultPreview.toString())
                break;
            } else {    /*brackets exists*/
                val o = bracketPair.openPos + 1
                val c = bracketPair.closePos
                /*  x*(y*c)/z -> x*res(y*c)/z -> x*res/z   */
                resultPreview.replace(o-1, c+1, calculate(resultPreview.substring(o, c)).toString())
            }
        }
        updatePreview()
    }

    private fun calculate(str: String):StringBuilder {
        println("calculate expressiop: $str")
        var res: Double = 0.0;
        var valuesList: MutableList<String> = str.split("\\*|\\-|\\+|\\/".toRegex()).toMutableList()
        var signsList = StringBuilder(str.replace("\\d|\\.".toRegex(), ""))
        var signMult = signsList.indexOf('*')
        var signDiv = signsList.indexOf('/')
        while (signMult != -1 || signDiv != -1) {
            if (signMult != -1 && signDiv != -1) {
                if (signMult < signDiv) {
                    res = valuesList[signMult].toDouble() * valuesList[signMult + 1].toDouble()
                    valuesList.removeAt(signMult + 1)
                    valuesList.removeAt(signMult)
                    signsList.deleteCharAt(signMult)
                    valuesList.add(signMult, res.toString())
                } else {
                    res = valuesList[signDiv].toDouble() / valuesList[signDiv + 1].toDouble()
                    valuesList.removeAt(signDiv + 1)
                    valuesList.removeAt(signDiv)
                    signsList.deleteCharAt(signDiv)
                    valuesList.add(signDiv, res.toString())
                }
            } else if (signDiv != -1) {
                res = valuesList[signDiv].toDouble() / valuesList[signDiv + 1].toDouble()
                valuesList.removeAt(signDiv + 1)
                valuesList.removeAt(signDiv)
                signsList.deleteCharAt(signDiv)
                valuesList.add(signDiv, res.toString())
            } else if (signMult != -1) {
                res = valuesList[signMult].toDouble() * valuesList[signMult + 1].toDouble()
                valuesList.removeAt(signMult + 1)
                valuesList.removeAt(signMult)
                signsList.deleteCharAt(signMult)
                valuesList.add(signMult, res.toString())
            }
            signMult = signsList.indexOf('*')
            signDiv = signsList.indexOf('/')
        }

        var signPlus = signsList.indexOf('+')
        var signMinus = signsList.indexOf('-')
        while (signPlus != -1 || signMinus != -1) {
            if (signPlus != -1 && signMinus != -1) {
                if (signPlus > signMinus) {
                    res = valuesList[signMinus].toDouble() - valuesList[signMinus + 1].toDouble()
                    valuesList.removeAt(signMinus + 1)
                    valuesList.removeAt(signMinus)
                    signsList.deleteCharAt(signMinus)
                    valuesList.add(signMinus, res.toString())
                } else {
                    res = valuesList[signPlus].toDouble() + valuesList[signPlus + 1].toDouble()
                    valuesList.removeAt(signPlus + 1)
                    valuesList.removeAt(signPlus)
                    signsList.deleteCharAt(signPlus)
                    valuesList.add(signPlus, res.toString())
                }
            } else if(signPlus != -1) {
                res = valuesList[signPlus].toDouble() + valuesList[signPlus + 1].toDouble()
                valuesList.removeAt(signPlus + 1)
                valuesList.removeAt(signPlus)
                signsList.deleteCharAt(signPlus)
                valuesList.add(signPlus, res.toString())
            } else if(signMinus != -1) {
                res = valuesList[signMinus].toDouble() - valuesList[signMinus + 1].toDouble()
                valuesList.removeAt(signMinus + 1)
                valuesList.removeAt(signMinus)
                signsList.deleteCharAt(signMinus)
                valuesList.add(signMinus, res.toString())
            }
            signPlus = signsList.indexOf('+')
            signMinus = signsList.indexOf('-')
        }

        return StringBuilder(res.toString())
    }

    private fun getDeepestBrackets(): BracketPositions {
        var depth = 0
        var maxDepthStart = 0
        var maxDepthEnd = 0
        var depthMax = 0
        var bc = 0
        for ((index, i) in resultPreview.withIndex()) {
            if (i == '(') {
                bc++
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
                bc--
            }
        }
        return BracketPositions(maxDepthStart, maxDepthEnd, bc)
    }
}
