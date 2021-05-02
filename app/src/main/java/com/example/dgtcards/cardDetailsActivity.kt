package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_transaction_details.*
import kotlinx.android.synthetic.main.card.*

class cardDetailsActivity : AppCompatActivity() {

    var itemModel:cardItemModel ? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        itemModel = intent.getSerializableExtra("data") as cardItemModel;

        city_icon.setImageResource(itemModel!!.image)
        city_name.text = itemModel!!.city
        balance_amount.text = itemModel!!.balance
        card_id.text = itemModel!!.id
        expire_day.text = "Expire: " + itemModel!!.expireDay
        created_time.text = "Created Time: "+ itemModel!!.createTime
        card_background.setBackgroundColor(itemModel!!.cardBackground)
        page_title.text = itemModel!!.city
    }
}