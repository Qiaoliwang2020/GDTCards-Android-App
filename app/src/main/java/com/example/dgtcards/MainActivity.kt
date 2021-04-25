package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.dgtcards.homeActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getStart(view: View) {

        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.login_layout,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        dialog.setContentView(view)
        dialog.show()


        val signIn = view.findViewById<Button>(R.id.login)

        signIn.setOnClickListener({
            val intent = Intent(this, homeActivity::class.java).apply {}
            startActivity(intent)
        })


        val linkToSignUp = view.findViewById<TextView>(R.id.linkToSignUp)
        linkToSignUp.setOnClickListener({

            dialog.dismiss();

            val signUpView = layoutInflater.inflate(R.layout.register_layout,null)

            dialog.setContentView(signUpView)
            dialog.show()
        })


        close.setOnClickListener({
            dialog.dismiss()
        })

    }

}