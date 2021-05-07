package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getStart(view: View) {

        val dialog = BottomSheetDialog(this)


        val view = layoutInflater.inflate(R.layout.login_layout,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener({
            dialog.dismiss()
        })

        dialog.setContentView(view)
        dialog.show()


        val signIn = view.findViewById<Button>(R.id.login)

        signIn.setOnClickListener({
            val intent = Intent(this, HomeActivity::class.java).apply {}
            startActivity(intent)
        })


        val linkToSignUp = view.findViewById<TextView>(R.id.linkToSignUp)
        linkToSignUp.setOnClickListener({

            dialog.dismiss();

            val signUpdialog = BottomSheetDialog(this)

            val offsetFromTop = 170
            signUpdialog?.behavior?.apply {
                isFitToContents = false
                setExpandedOffset(offsetFromTop)
                state = BottomSheetBehavior.STATE_EXPANDED
            }

            val signUpView = layoutInflater.inflate(R.layout.register_layout,null)

            val signUpClose = signUpView.findViewById<ImageView>(R.id.signUp_close)

            signUpClose.setOnClickListener({
                signUpdialog.dismiss()
            })

            signUpdialog.setContentView(signUpView)
            signUpdialog.show()
        })

    }

}