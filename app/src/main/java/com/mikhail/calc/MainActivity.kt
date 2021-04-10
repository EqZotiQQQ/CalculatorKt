package com.mikhail.calc

import android.nfc.Tag
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
 * replace EditText with something??
 *  logarithms
 * Сделать префиксную запись??
 * sqlite for hist
 *  BUG: enter sequence of -- ++ ** etc
 *  sin and cos in rads
 *  Алгоритм какой:
 *  1. Проверить выражение на корректность
 *  2. Взять глубочайшую пару скобок
 *  3. Посчитать её и результат вставить вместо выражения
 *  4. Если выражение функция - посчитать аргумент и применить функцию
 *  5. Делать так до тех пор, пока не останется мат. действий и функций
 * */


class MainActivity : FragmentActivity() {
    private val TAG: String = "TEST_LOG"
    // private var precision: MathContext = MathContext(5)        /*Need to add key for this option. By default 5*/

    private lateinit var fragments: MutableList<Fragment>

    val calculator = CalculatorImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragments()
        onNextButtonGrid()
        var neg = "-55"
        var i = abs(neg.toDouble())
        Log.d(TAG, "${sin(90f)}")
    }

    private fun initFragments() {
        fragments = mutableListOf(
            MainButtonGridFragment.newInstance(),
            AdditionalButtonGrid.newInstance()
        )
    }

    fun onNextButtonGrid(i: Int = 0) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragments[i]).commit()
    }

     fun onNewEntry(entry: String) {
        calculator.append(entry)
        onExpressionWindowUpdate(calculator.getExpression())
    }

     fun onPreviewUpdate(preview: String) {
         tvResultPreview.text = preview
         Log.d(TAG, "Update preview to $preview, ${tvResultPreview.text}")
    }

    private fun onExpressionWindowUpdate(expression: String) {
        etCalculationWindow.text = expression
    }

    fun removeLastSymbol() {
        calculator.deleteLast()
        onExpressionWindowUpdate(calculator.getExpression())
    }

     fun clearExpression() {
        calculator.reset()
        etCalculationWindow.text = null
        tvResultPreview.text = null
    }

    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
