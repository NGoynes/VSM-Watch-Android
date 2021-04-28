package com.example.vsmwatchandroidapplication.ui.logging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.lf
import android.widget.Toast
import java.io.File

class DeleteLog : AppCompatActivity() {

    private lateinit var filesList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_log)

        val extras = intent.extras
        val files: Array<out File> = extras?.get("files") as Array<out File>
        val removeDir = "/data/user/0/com.example.vsmwatchandroidapplication/files/"
        val filesWithoutDir: ArrayList<String> = ArrayList()
        for (i in files) {
            filesWithoutDir.add(i.toString().replace(removeDir, ""))
        }

        filesList = findViewById(R.id.files_list)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesWithoutDir)
        filesList.adapter = adapter

        filesList.setOnItemClickListener{ parent, _, position, _ ->
            (lf as LoggingFragment).delete(parent.getItemAtPosition(position).toString())
            Toast.makeText(this, "Item has been deleted. Please refresh to update list", Toast.LENGTH_SHORT).show()
        }
    }
}