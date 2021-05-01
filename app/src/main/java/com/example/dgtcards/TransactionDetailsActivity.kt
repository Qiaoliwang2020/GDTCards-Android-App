package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {

    var itemModel:transactionItemModel ? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        itemModel = intent.getSerializableExtra("data") as transactionItemModel;

        imageView.setImageResource(itemModel!!.image)
        transaction_title.text = itemModel!!.name
        transaction_city.text = itemModel!!.city
        transaction_amount.text = itemModel!!.cost
        transaction_id.text = "Receipt No:"+ itemModel!!.id
    }
}