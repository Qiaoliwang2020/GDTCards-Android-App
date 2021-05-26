package com.example.dgtcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_payment_success.*

class PaymentSuccess : AppCompatActivity() {
    var paymentInfo:PaymentInfo ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        // get data from confirmPayment activity
        paymentInfo = intent.getSerializableExtra("extra_paymentInfo") as PaymentInfo

        // show data to the page
        transaction_city.text = paymentInfo!!.type
        transaction_amount.text = "$" + paymentInfo!!.amount
        transaction_title.text = paymentInfo!!.type +" - "+ paymentInfo!!.city
        transaction_id.text = "Receipt No:" + paymentInfo!!.reciptNumber

    }
    // back to home
    fun navigateHome(view: View){
        val intent = Intent(this, HomeActivity::class.java).apply {}
        startActivity(intent)
    }
}