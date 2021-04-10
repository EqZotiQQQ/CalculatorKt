package com.mikhail.calc

import android.util.Log
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.*

class CalculatorImpl(val mainActivity: MainActivity) {

    private val TAG = "TEST_LOG"
    private var expression = StringBuilder()
    private var resultPreview = StringBuilder()
    private var precision: MathContext = MathContext(5)        /*Need to add key for this option. By default 5*/
    data class BracketPositions(val o: Int, val c: Int)

    fun append(entry: String) {
        expression.append(entry)
        //try {
            parseExpression()
        //} catch (e: Exception) {
        //    Log.e(TAG, e.message)
        //}
    }

    fun parseExpression() {
        if (!isBracketsCorrect() || !isCorrect()) {
            return
        }
        var we = StringBuilder(expression)
        replaceMinusToPlus(we)
        addMultSignBeforeOpenBracket(expression)
        addMultSignBeforeOpenBracket(we)
        while(we.contains("[*\\-+/()]|\\w".toRegex())) {
            val deepestBrackets = getDeepestBrackets(we.toString())
            Log.d(TAG, "deepest brackets: ${deepestBrackets.o}, ${deepestBrackets.c}, expression: $we, expression is correct!")
            if (deepestBrackets.c == 0) {   // If no brackets - calculate!
                we = calculate(we.toString())
                break
            } else { // else - get deepest brackets and calculate value inside then replace it
                we.replace(deepestBrackets.o, deepestBrackets.c + 1, calculate(we.substring(deepestBrackets.o + 1, deepestBrackets.c)).toString())
            }
        }
        Log.d(TAG, "Calculation finished $we")
        mainActivity.onPreviewUpdate(we.toString())
    }

    private fun getDeepestBrackets(str: String): BracketPositions {
        var deepestOpenPos = 0
        var deepestClosePos = 0
        var currentDepth = 0
        var maxDepth = 0
        var closed = true
        for ((i, c) in str.withIndex()) {
            if (c == '(') {
                currentDepth++
                if (currentDepth > maxDepth) {
                    deepestOpenPos = i
                    maxDepth = currentDepth
                    closed = false
                }
            }
            if (c == ')') {
                if(!closed) {
                    deepestClosePos = i
                    closed = true
                }
                currentDepth--
            }
        }
        return BracketPositions(deepestOpenPos, deepestClosePos)
    }

    /**
     * Calculates. In this function no nested brackets
     */
    private fun calculate(str: String):StringBuilder {
        Log.d(TAG, "calculate:string: $str")
        val values = str.split("[+|*/]".toRegex()).toMutableList()                       // extract digits: [1, 2, 5, 66, ...]
        val signs = StringBuilder(str.replace("\\d|\\.|-|%|\\w".toRegex(), ""))  // extract signs:   [+, *, \, \, ...]
        //val functions = extractFunctions(values)                                                  // extract math functions: [abs, rad, ...]
        Log.d(TAG, "values: $values; signs: $signs")

        var multPos = signs.indexOf('*')
        var divPos = signs.indexOf('/')
        var sign = 0

        calculateFuncs(values)

        var bd = BigDecimal(values[0])
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
            bd = values[sign].toBigDecimal().add(values[sign + 1].toBigDecimal(), precision)    //breaks here
            values.removeAt(sign + 1)
            values.removeAt(sign)
            signs.deleteCharAt(sign)
            values.add(sign, bd.toString())
            addPos = signs.indexOf('+')
        }
        return StringBuilder(bd.toString())
    }

    fun calculateFuncs(values: MutableList<String>) {
        for((i, v) in values.withIndex()) {
            if (v.contains("[a-z]".toRegex())) {
                val arg = v.filter { it.isDigit() || it == '-'}.toDouble()
                val func = v.filter { it.isLetter() }
                values[i] = when(func) {
                    //"deg" ->
                    "rad" -> ((180 / PI) * arg).toString()
                    //"pow" ->
                    "sin" -> sin(   arg).toString()
                    "cos" -> cos(   arg).toString()
                    "tg"  -> tan(   arg).toString()
                    "ctg" -> (1/tan(arg)).toString()
                    "abs" -> abs(   arg).toString()
                    else  -> "0"
                }
            }
        }
    }

    /**
     * perform smth like this 5+6-9 to 5+6+-9
     */
    private fun replaceMinusToPlus(str: StringBuilder)  {
        for((i, s) in str.withIndex()) {
            if(s == '-') {
                if (i > 0) {
                    if (str[i-1].isDigit() || str[i-1] == '%') {
                        str.insert(i,'+')
                    }
                }
            }
        }
    }

    /**
     * perform smth like this 5+6(4-9) to 5+6*(4-9)
     */
    fun addMultSignBeforeOpenBracket(str: StringBuilder) {
        for ((i, s) in str.withIndex()) {
            if (s == '(') {
                if (i > 1 && str[i - 1].isDigit()) {
                    str.insert(i, '*')
                }
            }
        }
    }

    /**
     * Calculate all percentages functions
     */
    private fun calculatePercentage(values: MutableList<String>) {
        for((i, s) in values.withIndex()) {
            if(s.last() == '%') {
                val sb = StringBuilder(values[i])
                values[i] = sb.deleteCharAt(sb.lastIndex).toString()
                val multiplier = values[i-1].toBigDecimal().divide(BigDecimal(100))
                values[i] = values[i].toBigDecimal().multiply(multiplier).toString()
            }
        }
    }

    fun getPreview(): String = resultPreview.toString()
    fun getExpression(): String = expression.toString()


    /**
     * check is expression correct.. Is it ends with digit or close bracket ')'
     */
    private fun isCorrect(): Boolean {
        val i = expression.takeLast(1).toString()
        if (i == "/" || i == "*" || i == "-" || i == "+" || !isBracketsCorrect() || i.contains("[a-z]".toRegex())) {
            Log.d(TAG, "Expression $expression isn't correct")
            return false
        }
        Log.d(TAG, "Expression $expression is correct")
        resultPreview = expression
        return true
    }

    /**
     * Check is number of '(' equals number of ')'
     */
    private fun isBracketsCorrect(): Boolean {
        var bracketNum = 0
        for (i in expression) {
            if (bracketNum < 0) {
                return false
            }
            if (i == '(') {
                bracketNum++
            }
            if (i == ')') {
                bracketNum--
            }
        }
        if (bracketNum == 0) {
            return true
        }
        return false
    }

    fun reset() = expression.clear()

    fun deleteLast() {
        if (expression.length < 1) {
            return
        }
        expression.deleteCharAt(expression.length - 1)
        parseExpression()
    }
}