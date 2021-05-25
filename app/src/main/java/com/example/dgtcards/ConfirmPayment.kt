package com.example.dgtcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_confirm_payment.*
import kotlinx.android.synthetic.main.activity_confirm_payment.transaction_amount
import kotlinx.android.synthetic.main.activity_payment_success.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

class ConfirmPayment : AppCompatActivity() {

    private lateinit var dbRefPayments : DatabaseReference
    private lateinit var dbRefCards : DatabaseReference

    var paymentInfo:PaymentInfo ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_payment)

        paymentInfo = intent.getSerializableExtra("extra_paymentInfo") as PaymentInfo
        payment_type.text = paymentInfo!!.type + " - " + paymentInfo!!.city
        transaction_amount.text = "$" + paymentInfo!!.amount
        if(paymentInfo!!.type == "withdraw"){
            payment_title.text = "Withdraw"
            action_btn.text = "withdraw now"
        }

    }

    fun payNow(view: View) {
        val current = LocalDateTime.now()
        val dateFormatter = ofPattern("dd/MM/yyyy HH:mm:ss")
        val numberFormatter= ofPattern("yyyyMMddHHmmss")
        paymentInfo?.createTime = current.format(dateFormatter)
        paymentInfo?.notes = payment_notes.text.toString()
        paymentInfo?.id = paymentInfo!!.type + paymentInfo!!.cardId + current.format(numberFormatter).toString()
        val id = paymentInfo!!.id
        val cardId = paymentInfo!!.cardId
        val cardBalance = paymentInfo!!.cardBalance
        var amount: Double ? = null

        amount = if(paymentInfo!!.type == "recharge"){
            paymentInfo!!.amount?.toDouble()?.plus(cardBalance!!);
        }else{
            cardBalance!! - paymentInfo!!.amount?.toDouble()!!;
        }

        paymentInfo?.reciptNumber = paymentInfo!!.type+ current.format(numberFormatter).toString()

        dbRefPayments = FirebaseDatabase.getInstance().getReference("Payments")
        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")

        if (id != null) {
            dbRefPayments.child(id).setValue(paymentInfo).addOnSuccessListener {
                if (cardId != null && amount != null) {
                    dbRefCards.child(cardId).child("balance").setValue(amount)
                    val intent = Intent(this, PaymentSuccess::class.java).apply {}
                    intent.putExtra("extra_paymentInfo",paymentInfo)
                    startActivity(intent)
                }
            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
        }
    }
}

