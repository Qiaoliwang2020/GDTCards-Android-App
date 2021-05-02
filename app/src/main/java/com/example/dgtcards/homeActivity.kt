package com.example.dgtcards

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home.*


class homeActivity : AppCompatActivity(),transactionItemAdapter.ClickTransactionItem,cardItemAdapter.ClickCardItem {

    // card list
    var cardListModel = arrayOf(
            cardItemModel(R.drawable.melbourne,"$25.00","Melbourne","N03787437670","22 Apr 2022","22 Apr 2021",R.color.design_default_color_primary),
            cardItemModel(R.drawable.sydney_opera_house,"$20.00","Sydney","Sy03787437670","22 Apr 2022","22 Apr 2021",R.color.teal_200),
            cardItemModel(R.drawable.new_york,"$45.00","New York","NYC03787437670","22 Apr 2022","22 Apr 2021",R.color.green_700)
    )
    var cardItemModelList = ArrayList<cardItemModel>();
    var cardItemAdapter : cardItemAdapter ? = null;

    // transaction list
    var transactionListModel = arrayOf(
        transactionItemModel(R.drawable.payment_method,"Flinders Street - Deakin University","15 Mar 2021","-$4.50","Melbourne","N0378743767"),
        transactionItemModel(R.drawable.public_transport,"Southern Cross Station - Flinder Street","15 Mar 2021","-$5.0","Melbourne","N03787232767"),
        transactionItemModel(R.drawable.payment_method,"Queens Street - Brooklyn Main Street","5 Mar 2020","-$6.0","NewYork","N03787123e671")
    )
    var transactionItemModelList = ArrayList<transactionItemModel>();
    var transactionItemAdapter : transactionItemAdapter ? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // card list
        for (item in cardListModel){
            cardItemModelList.add(item)
        }
        cards.layoutManager = LinearLayoutManager(this)
        cards.setHasFixedSize(true)
        cardItemAdapter = cardItemAdapter(this)
        cardItemAdapter!!.setData(cardItemModelList)
        cards.adapter = cardItemAdapter

        // transaction list
        for (item in transactionListModel){
            transactionItemModelList.add(item)
        }
        transaction_history_list.layoutManager = LinearLayoutManager(this)
        transaction_history_list.setHasFixedSize(true)
        transactionItemAdapter = transactionItemAdapter(this)
        transactionItemAdapter!!.setData(transactionItemModelList)
        transaction_history_list.adapter = transactionItemAdapter
    }


    override fun ClickTransactionItem(itemModel: transactionItemModel) {

        var transactionItem = itemModel;
        startActivity(Intent(this@homeActivity,TransactionDetailsActivity::class.java).putExtra("data",transactionItem))
    }

    override fun ClickCardItem(itemModel: cardItemModel) {
        var cardItem = itemModel;
        startActivity(Intent(this@homeActivity,cardDetailsActivity::class.java).putExtra("data",cardItem))
    }
}