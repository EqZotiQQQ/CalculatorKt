package com.mikhail.calc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MainButtonGridFragment : Fragment(), View.OnClickListener {

    private val TAG = "TEST_LOG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.main_button_grid_fragment, container, false)
        initButtons(rootView)
        return rootView
    }

    /**
     * Initialize buttons for digit grid
     */
    private fun initButtons(rootView: View) {
        val num0 = rootView.findViewById<Button>(R.id.btn0).setOnClickListener(this)
        val num1 = rootView.findViewById<Button>(R.id.btn1).setOnClickListener(this)
        val num2 = rootView.findViewById<Button>(R.id.btn2).setOnClickListener(this)
        val num3 = rootView.findViewById<Button>(R.id.btn3).setOnClickListener(this)
        val num4 = rootView.findViewById<Button>(R.id.btn4).setOnClickListener(this)
        val num5 = rootView.findViewById<Button>(R.id.btn5).setOnClickListener(this)
        val num6 = rootView.findViewById<Button>(R.id.btn6).setOnClickListener(this)
        val num7 = rootView.findViewById<Button>(R.id.btn7).setOnClickListener(this)
        val num8 = rootView.findViewById<Button>(R.id.btn8).setOnClickListener(this)
        val num9 = rootView.findViewById<Button>(R.id.btn9).setOnClickListener(this)
        val btnDot = rootView.findViewById<Button>(R.id.btnDot).setOnClickListener(this)
        val btnDivide = rootView.findViewById<Button>(R.id.btnDivide).setOnClickListener(this)
        val btnMultiply = rootView.findViewById<Button>(R.id.btnMultiply).setOnClickListener(this)
        val btnPlus = rootView.findViewById<Button>(R.id.btnPlus).setOnClickListener(this)
        val btnSubtract = rootView.findViewById<Button>(R.id.btnSubtract).setOnClickListener(this)
        val btnOpenBrace = rootView.findViewById<Button>(R.id.btnOpenBrace).setOnClickListener(this)
        val btnCloseBrace = rootView.findViewById<Button>(R.id.btnCloseBrace).setOnClickListener(this)
        val btnPersent = rootView.findViewById<Button>(R.id.btnPersent).setOnClickListener(this)
        val btnChangeFragment = rootView.findViewById<Button>(R.id.btnChangeFragment).setOnClickListener(this)
        val btnClear = rootView.findViewById<Button>(R.id.btnClear).setOnClickListener(this)
        val btnRemoveSymbol = rootView.findViewById<Button>(R.id.btnRemoveSymbol).setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainButtonGridFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun onClick(v: View) {
        val entry = when (v.id) {
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
            R.id.btnChangeFragment -> {
                Log.d(TAG, "Next grid")
                (activity as MainActivity).onNextButtonGrid(1)
            }
            R.id.btnClear -> {
                (activity as MainActivity).clearExpression()
            }

            R.id.btnRemoveSymbol -> {
                (activity as MainActivity).removeLastSymbol()
            }

            R.id.btnEquals -> {
                //fullExpression = resultPreview
            }
        }
        if(entry != null) {
            (activity as MainActivity).onNewEntry(entry)
        }
    }
}
