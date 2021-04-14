package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.EditText
import com.example.dgtcards.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getStart(view: View) {

        val intent = Intent(this, LoginActivity::class.java).apply {}
        startActivity(intent)
    }
}