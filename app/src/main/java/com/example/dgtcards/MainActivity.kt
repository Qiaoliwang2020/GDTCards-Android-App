package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.login_layout.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun loginFormValidate(view: View):Boolean{
        // login form
        val userAccount = view.findViewById<EditText>(R.id.userAccountInput)
        val loginPassword = view.findViewById<EditText>(R.id.loginPasswordInput)

        // login validate
        if(userAccount.text.toString().isEmpty()){
            userAccount.error ="user account should not be blank"
            return false
        }else if(loginPassword.text.toString().isEmpty()){
            loginPassword.error ="password should not be blank"
            return false
        }
        return true
    }
    private fun registerFormValidate(view: View):Boolean{

        // register form
        val fullName = view.findViewById<EditText>(R.id.fullNameInput)
        val email = view.findViewById<EditText>(R.id.emailInput)
        val phone = view.findViewById<EditText>(R.id.phoneInput)
        val registerPassword = view.findViewById<EditText>(R.id.passwordInput)
        val confirmPassword = view.findViewById<EditText>(R.id.confirmPasswordInput)

        if(fullName.text.toString().isEmpty()){
            fullName.error = "Your name should not be blank"
            return false
        }else if(email.text.toString().isEmpty()){
            email.error = "email should not be blank"
            return false
        }else if(registerPassword.text.toString().isEmpty()){
            registerPassword.error = "password should not be blank"
            return false
        }else if (confirmPassword.text.toString().isEmpty()){
            confirmPassword.error = "confirm Password should not be blank"
            return false
        }else if (confirmPassword.text.toString() != registerPassword.text.toString()){
            confirmPassword.error = "Password and confirm password does not match"
            return false
        }

        return true
    }

    fun getStart(view: View) {

        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.login_layout,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener{
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()


        val signIn = view.findViewById<Button>(R.id.login)

        signIn.setOnClickListener{

            if(loginFormValidate(view)){
                val intent = Intent(this, HomeActivity::class.java).apply {}
                startActivity(intent)
            }

        }


        val linkToSignUp = view.findViewById<TextView>(R.id.linkToSignUp)
        linkToSignUp.setOnClickListener{

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

            signUpClose.setOnClickListener{
                signUpdialog.dismiss()
            }

            signUpdialog.setContentView(signUpView)
            signUpdialog.show()

            val signUp = signUpView.findViewById<Button>(R.id.signUp)

            signUp.setOnClickListener{
                registerFormValidate(signUpView)
            }
        }

    }

}