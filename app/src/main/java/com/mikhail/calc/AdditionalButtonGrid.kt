package com.mikhail.calc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AdditionalButtonGrid : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.additional_button_grid_fragment, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdditionalButtonGrid().apply {
            arguments = Bundle().apply {}
        }
    }
}
