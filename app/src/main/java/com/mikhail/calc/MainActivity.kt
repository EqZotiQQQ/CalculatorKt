package com.mikhail.calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.Math.pow
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.*

/**
 * TODO:
 * replace EditText with something else what
 *  logarithms
 *
 * */


class MainActivity : FragmentActivity() {
    private val TAG: String = "Calculator"
    data class BracketPositions(val openPos: Int, val closePos: Int, val bc: Boolean)
    private var fullExpression = StringBuilder()
    private var resultPreview = StringBuilder()
    private var precision: MathContext = MathContext(5)        /*Need to add key for this option. By default 5*/
    var mainFragment: Fragment? = null
    var additionalFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment(0)
    }

    private fun changeFragment(i: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (mainFragment == null) {
            mainFragment = MainButtonGridFragment.newInstance()
        }
        if (additionalFragment == null) {
            additionalFragment = AdditionalButtonGrid.newInstance()
        }
        if (i == 0) {
            fragmentTransaction.replace(R.id.container, mainFragment!!)
            fragmentTransaction.commit()
        } else if (i == 1) {
            fragmentTransaction.replace(R.id.container, additionalFragment!!)
            fragmentTransaction.commit()
        }
        Log.d(TAG, "Change fragment")
    }

    fun onClick(v: View) {
        /*main fragment*/
        Log.d(TAG, "onClick(${v})")
        val value = when (v.id) {
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
            else -> null
        }

        when (v.id) {
            R.id.btnChangeMainToAdditionalFragment -> {
                changeFragment(1)
            }
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

        /*Additional fragment*/
        when(v.id) {
            R.id.btnChangeAdditionalToMainFragment -> {
                changeFragment(0)
            }
        }
        val additionalValue = when(v.id) {
            R.id.btnToDegree->"deg("
            R.id.btnToRad->"rad("
            R.id.btnSqrt->"sqrt("
            R.id.btnPow->"pow("
            R.id.btnPi->"Pi("
            R.id.btnE->"e("
            R.id.btnAbs->"abs("
            R.id.btnSin->"sin("
            R.id.btnCos->"cos("
            R.id.btnTg->"tg("
            R.id.btnCtg->"ctg("
            else->null
        }

        if(additionalValue != null) {
            fullExpression.append(additionalValue)
        }
        if(value != null) {
            fullExpression.append(value)
        }
        updateCalcWindow()
        Log.d(TAG, "132")
        if(isCorrect()) {
            try {
                Log.d(TAG, "135")
                parseExpression()
            } catch (exc: Exception) {
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
        etCalculationWindow.text = fullExpression.toString()
    }

    private fun parseExpression() {
        Log.d(TAG, "parseExpression($resultPreview)")
        while (resultPreview.contains("[*\\-+/()]".toRegex())) {
            val bracketPair: BracketPositions = getDeepestBrackets(resultPreview.toString())
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
        var string = replaceMinusToPlus(str)
        Log.d(TAG, "calculate expression: $string")
        val values = string.split("[+|*/]".toRegex()).toMutableList()
        val signs = StringBuilder(string.replace("\\d|\\.|-|%".toRegex(), ""))
        var multPos = signs.indexOf('*')
        var divPos = signs.indexOf('/')
        var sign = 0

        calculateToDegree(values)
        calculateToRadian(values)
        calculateSqrt(values)
        calculatePows(values)
        calculateAbs(values)
        calculateSin(values)
        calculateCos(values)
        calculateTg(values)
        calculateCtg(values)

        var bd = BigDecimal(values[0])
        Log.d(TAG, "values: ${values.toString()}")
        Log.d(TAG, "signs: $signs")

        calculatePercentage(values)
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


    private fun calculateToDegree(values: MutableList<String>) {
        if(values.contains("deg")) {

        }
    }

    private fun calculateToRadian(values: MutableList<String>) {
        if(values.contains("rad")) {

        }
    }

    private fun calculateSqrt(values: MutableList<String>) {
        if(values.contains("sqrt")) {

        }
    }

    private fun calculatePows(values: MutableList<String>) {
        if(values.contains("pow")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("pow")) {
                    val brackets = getDeepestBrackets(value)
                    /*a - val, b - exp*/
                    /*pow(values[i-1], values[i]*/
                }
            }
        }
    }

    private fun calculateAbs(values: MutableList<String>) {
        if(values.contains("abs")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("abs")) {
                    val brackets = getDeepestBrackets(value)
                    values[index] = abs(value.substring(brackets.openPos+1, brackets.closePos).toDouble()).toString()
                }
            }
        }
    }

    private fun calculateSin(values: MutableList<String>) {
        if(values.contains("sin")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("sin")) {
                    val brackets = getDeepestBrackets(value)
                    values[index] = sin(value.substring(brackets.openPos+1, brackets.closePos).toDouble()).toString()
                }
            }
        }
    }

    private fun calculateCos(values: MutableList<String>) {
        if(values.contains("cos")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("cos")) {
                    val brackets = getDeepestBrackets(value)
                    values[index] = cos(value.substring(brackets.openPos+1, brackets.closePos).toDouble()).toString()
                }
            }
        }
    }

    private fun calculateTg(values: MutableList<String>) {
        if(values.contains("tg")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("tan")) {
                    val brackets = getDeepestBrackets(value)
                    values[index] = tan(value.substring(brackets.openPos+1, brackets.closePos).toDouble()).toString()
                }
            }
        }
    }

    private fun calculateCtg(values: MutableList<String>) {
        if(fullExpression.contains("ctg")) {
            for((index, value) in values.withIndex()) {
                if(value.contains("ctg")) {
                    val brackets = getDeepestBrackets(value)
                    /**/
                }
            }
        }
    }

    private fun replaceMinusToPlus(str: String): StringBuilder {
        var string = StringBuilder(str)
        for((i, s) in string.withIndex()) {
            if(s == '-') {
                if (i > 0) {
                    if (string[i-1].isDigit() || string[i-1] == '%') {
                        string.insert(i,'+')
                    }
                }
            }
        }
        return string
    }

    private fun calculatePercentage(values: MutableList<String>) {
        for((i, s) in values.withIndex()) {
            if(s.last() == '%') {
                var sb = StringBuilder(values[i])
                values[i] = sb.deleteCharAt(sb.lastIndex).toString()
                var multiplier = values[i-1].toBigDecimal().divide(BigDecimal(100))
                values[i] = values[i].toBigDecimal().multiply(multiplier).toString()
            }
        }
    }

    private fun getDeepestBrackets(str: String?): BracketPositions {
        val string = if (str == null) {
            resultPreview
        } else {
            StringBuilder(str)
        }
        var depth = 0
        var maxDepthStart = 0
        var maxDepthEnd = 0
        var depthMax = 0
        for ((index, i) in string.withIndex()) {
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
