package com.example.dgtcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {

    var itemModel:TransactionItemModel ? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        // get data from home activity
        itemModel = intent.getSerializableExtra("data") as TransactionItemModel;

        // show data to the page
        imageView.setImageResource(itemModel!!.image!!)
        transaction_title.text = itemModel!!.type!!.uppercase()
        transaction_city.text = itemModel!!.city
        transaction_amount.text = "$" + itemModel!!.amount
        transaction_id.text = "Receipt No:"+ itemModel!!.reciptNumber
    }

    // back to home
    fun navigateHome(view: View){
        val intent = Intent(this, HomeActivity::class.java).apply {}
        startActivity(intent)
    }
}