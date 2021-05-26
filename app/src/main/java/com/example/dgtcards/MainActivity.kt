package com.example.dgtcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login_layout.*


class MainActivity : AppCompatActivity() {

    // initialize auth and database
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // access to firebase Users table
        dbref = FirebaseDatabase.getInstance().getReference("Users")

    }
    // validate for login form
    private fun loginFormValidate(view: View):Boolean{
        // find EditText of login account and password
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

    // validate for register form
    private fun registerFormValidate(view: View):Boolean{

        // get each EditText of register form
        val fullName = view.findViewById<EditText>(R.id.fullNameInput)
        val email = view.findViewById<EditText>(R.id.emailInput)
        val phone = view.findViewById<EditText>(R.id.phoneInput)
        val registerPassword = view.findViewById<EditText>(R.id.passwordInput)
        val confirmPassword = view.findViewById<EditText>(R.id.confirmPasswordInput)

        // identify the value is empty or not
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

    /**
     *  when user click get start open login dialog
     */
    fun getStart(view: View) {

        // create bottom sheet dialog
        val dialog = BottomSheetDialog(this)

        // set the login_layout as the view in dialog
        val view = layoutInflater.inflate(R.layout.login_layout,null)

        // get close button
        val close = view.findViewById<ImageView>(R.id.iv_close)

        // when click close button close this dialog
        close.setOnClickListener{
            dialog.dismiss()
        }

        // set view and show dialog
        dialog.setContentView(view)
        dialog.show()

        // get sign in button
        val signIn = view.findViewById<Button>(R.id.login)

        // when user click to sign in
        signIn.setOnClickListener{

            if(loginFormValidate(view)){

                // get EditText of user account and password
                val userAccount = view.findViewById<EditText>(R.id.userAccountInput)
                val loginPassword = view.findViewById<EditText>(R.id.loginPasswordInput)

                // sign in with firebase using email and password function of firebase
                auth.signInWithEmailAndPassword(userAccount.text.toString(),loginPassword.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        // if successful navigate to home page
                        val intent = Intent(this, HomeActivity::class.java).apply {}
                        startActivity(intent)
                    }else{
                        // if failed show failed
                        Toast.makeText(this@MainActivity,"sign in failed, please try again!!",Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

        // get the button of sign up
        val linkToSignUp = view.findViewById<TextView>(R.id.linkToSignUp)

        // when user click to sign up (create an account)
        linkToSignUp.setOnClickListener{

            // close sign in dialog
            dialog.dismiss();

            // create a sign up dialog
            val signUpdialog = BottomSheetDialog(this)

            // set the distance from top and behavior
            val offsetFromTop = 170
            signUpdialog?.behavior?.apply {
                isFitToContents = false
                setExpandedOffset(offsetFromTop)
                state = BottomSheetBehavior.STATE_EXPANDED
            }

            // set register layout to sign up dialog
            val signUpView = layoutInflater.inflate(R.layout.register_layout,null)

            // get close button in sign up dialog
            val signUpClose = signUpView.findViewById<ImageView>(R.id.signUp_close)

            // when click close button, close the dialog
            signUpClose.setOnClickListener{
                signUpdialog.dismiss()
            }
            // set the content view and show dialog
            signUpdialog.setContentView(signUpView)
            signUpdialog.show()

            // get link to sign in button
            val linkToSignIn = signUpView.findViewById<TextView>(R.id.linkToLogin)

            // when user click sign in in sign up dialog, close sign up dialog and open sign in dialog
            linkToSignIn.setOnClickListener{
                signUpdialog.dismiss()
                dialog.show()
            }

            // get sign up button
            val signUp = signUpView.findViewById<Button>(R.id.signUp)

            // when user click sign up, apply for firebase function to create user with email and password.
            signUp.setOnClickListener{
                // validate  form
                if(registerFormValidate(signUpView)){

                    // get each EditText
                    val fullName = signUpView.findViewById<EditText>(R.id.fullNameInput)
                    val email = signUpView.findViewById<EditText>(R.id.emailInput)
                    val phone = signUpView.findViewById<EditText>(R.id.phoneInput)
                    val registerPassword = signUpView.findViewById<EditText>(R.id.passwordInput)

                    // create account with firebase by using email and password
                    auth.createUserWithEmailAndPassword(email.text.toString(),registerPassword.text.toString())
                            .addOnCompleteListener {
                                if(it.isSuccessful){ // if success insert the user data to database
                                    // get current user and create user data model
                                    val currentUser = auth.currentUser
                                    val user = UserModel(currentUser!!.uid,fullName.text.toString(),email.text.toString(),phone.text.toString(),registerPassword.text.toString())

                                    // insert the data to firebase databse
                                    dbref.child(currentUser!!.uid).setValue(user).addOnSuccessListener {
                                        // if success, clear the text on EditText
                                        fullName.text.clear()
                                        email.text.clear()
                                        phone.text.clear()
                                        registerPassword.text.clear()

                                    }.addOnFailureListener {
                                        // if failed show failed message
                                        Toast.makeText(this@MainActivity,"data save failed, please try again!!",Toast.LENGTH_LONG).show()
                                    }
                                    // show success message and close sign up dialog when user registration is successful
                                    Toast.makeText(this@MainActivity,"Registration success",Toast.LENGTH_LONG).show()
                                    signUpdialog.dismiss()

                                }else{
                                    // if registration failed show message
                                    Toast.makeText(this@MainActivity,"Registration failed, please try again!!",Toast.LENGTH_LONG).show()
                                }
                            }
                }
            }
        }

    }

}