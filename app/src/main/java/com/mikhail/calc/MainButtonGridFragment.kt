package com.mikhail.calc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MainButtonGridFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_button_grid_fragment, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainButtonGridFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}
