package com.example.dgtcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_confirm_payment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

class ConfirmPayment : AppCompatActivity() {

    // initialize database reference
    private lateinit var dbRefPayments : DatabaseReference
    private lateinit var dbRefCards : DatabaseReference

    // initialize a paymentInfo data model
    var paymentInfo:PaymentInfo ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_payment)

        // get data from cardDetailsActivity
        paymentInfo = intent.getSerializableExtra("extra_paymentInfo") as PaymentInfo
        // show the data to the page
        payment_type.text = paymentInfo!!.type + " - " + paymentInfo!!.city
        transaction_amount.text = "$" + paymentInfo!!.amount
        if(paymentInfo!!.type == "withdraw"){
            payment_title.text = "Withdraw"
            action_btn.text = "withdraw now"
        }

    }

    // when user click to pay or withdraw
    fun payNow(view: View) {
        // set current time and time formatter
        val current = LocalDateTime.now()
        val dateFormatter = ofPattern("dd/MM/yyyy HH:mm:ss")
        val numberFormatter= ofPattern("yyyyMMddHHmmss")

        // set the create time as current time
        paymentInfo?.createTime = current.format(dateFormatter)
        // set notes if user enter notes
        paymentInfo?.notes = payment_notes.text.toString()
        // generate a payment id
        paymentInfo?.id = paymentInfo!!.type + paymentInfo!!.cardId + current.format(numberFormatter).toString()
        val id = paymentInfo!!.id
        // get card id and balance
        val cardId = paymentInfo!!.cardId
        val cardBalance = paymentInfo!!.cardBalance
        var amount: Double ? = null

        // set final amount according to payment type (withdraw and recharge)
        amount = if(paymentInfo!!.type == "recharge"){
            paymentInfo!!.amount?.toDouble()?.plus(cardBalance!!);
        }else{
            cardBalance!! - paymentInfo!!.amount?.toDouble()!!;
        }

        // generate a receipt number
        paymentInfo?.reciptNumber = paymentInfo!!.type+ current.format(numberFormatter).toString()

        // access firebase database Payments and Cards table
        dbRefPayments = FirebaseDatabase.getInstance().getReference("Payments")
        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")

        // insert payment data to database
        if (id != null) {
            dbRefPayments.child(id).setValue(paymentInfo).addOnSuccessListener {
                if (cardId != null && amount != null) {
                    // if success, update the balance
                    dbRefCards.child(cardId).child("balance").setValue(amount)
                    // navigate to payment success page
                    val intent = Intent(this, PaymentSuccess::class.java).apply {}
                    intent.putExtra("extra_paymentInfo",paymentInfo)
                    startActivity(intent)
                }
            }.addOnFailureListener {
                // if failed show failed message
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
        }
    }
}

