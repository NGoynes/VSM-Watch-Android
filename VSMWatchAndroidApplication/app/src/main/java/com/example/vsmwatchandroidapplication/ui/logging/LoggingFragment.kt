package com.example.vsmwatchandroidapplication.ui.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.R

class LoggingFragment : Fragment() {

    private lateinit var loggingViewModel: LoggingViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // loggingViewModel =
                // ViewModelProvider(this).get(LoggingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_logging, container, false)
        // val textView: TextView = root.findViewById(R.id.text_logging)
        // loggingViewModel.text.observe(viewLifecycleOwner, Observer {
            // textView.text = it
        // })
        return root
    }
}