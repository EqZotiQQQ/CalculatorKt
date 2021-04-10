package com.mikhail.calc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class AdditionalButtonGrid : Fragment(), View.OnClickListener {

    private val TAG = "TEST_LOG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.additional_button_grid_fragment, container, false)
        initButtons(rootView)
        return rootView
    }

    /**
     * Initialize buttons for digit grid
     */
    private fun initButtons(rootView: View) {
        val btnHistBrace = rootView.findViewById<Button>(R.id.btnHistBrace).setOnClickListener(this)
        val btnToDegree = rootView.findViewById<Button>(R.id.btnToDegree).setOnClickListener(this)
        val btnToRad = rootView.findViewById<Button>(R.id.btnToRad).setOnClickListener(this)

        val btnSqrt = rootView.findViewById<Button>(R.id.btnSqrt).setOnClickListener(this)
        val btnPow = rootView.findViewById<Button>(R.id.btnPow).setOnClickListener(this)

        val btnPi = rootView.findViewById<Button>(R.id.btnPi).setOnClickListener(this)
        val btnE = rootView.findViewById<Button>(R.id.btnE).setOnClickListener(this)

        val btnAbs = rootView.findViewById<Button>(R.id.btnAbs).setOnClickListener(this)

        val btnChangeFragment = rootView.findViewById<Button>(R.id.btnChangeFragment).setOnClickListener(this)

        val btnSin = rootView.findViewById<Button>(R.id.btnSin).setOnClickListener(this)
        val btnCos = rootView.findViewById<Button>(R.id.btnCos).setOnClickListener(this)
        val btnTg = rootView.findViewById<Button>(R.id.btnTg).setOnClickListener(this)
        val btnCtg = rootView.findViewById<Button>(R.id.btnCtg).setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdditionalButtonGrid().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun onClick(v:View) {
        when (v.id) {
            R.id.btnChangeFragment -> {
                (activity as MainActivity).onNextButtonGrid()
                return
            }
        }

        val entry = when(v.id) {
            R.id.btnToDegree->"deg("
            R.id.btnToRad->"rad("
            R.id.btnSqrt->"sqrt("
            R.id.btnPow->"pow("
            R.id.btnPi->"Pi("
            R.id.btnE->"e"// возникнет проблема с парсом функций
            R.id.btnAbs->"abs("
            R.id.btnSin->"sin("
            R.id.btnCos->"cos("
            R.id.btnTg->"tg("
            R.id.btnCtg->"ctg("
            else->null
        }
        if(entry != null) {
            (activity as MainActivity).onNewEntry(entry)
        }
    }
}
