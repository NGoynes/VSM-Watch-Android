package com.example.vsmwatchandroidapplication.ui.logging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoggingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is logging Fragment"
    }
    val text: LiveData<String> = _text
}