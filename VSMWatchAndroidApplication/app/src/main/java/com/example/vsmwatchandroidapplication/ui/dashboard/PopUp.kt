package com.example.vsmwatchandroidapplication.ui.dashboard

import com.example.vsmwatchandroidapplication.R
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class PopUp : AppCompatActivity() {
    //PopupWindow display method
    fun showPopupWindow(view: View) {

        //Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.pop_up_layout, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        //Initialize the elements of our window, install the handler
        val test2 = popupView.findViewById<TextView>(R.id.titleText)
        test2.setText(R.string.textTitle)
        val buttonEdit = popupView.findViewById<Button>(R.id.messageButton)
        buttonEdit.setOnClickListener { //As an example, display the message
            popupWindow.dismiss()
        }


        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }
    }
}