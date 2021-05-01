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


class homeActivity : AppCompatActivity(),transactionItemAdapter.ClickTransactionItem {

    // transaction list
    var itemListModel = arrayOf(
        transactionItemModel(R.drawable.payment_method,"Flinders Street - Deakin University","15 Mar 2021","-$4.50","Melbourne","N0378743767"),
        transactionItemModel(R.drawable.public_transport,"Southern Cross Station - Flinder Street","15 Mar 2021","-$5.0","Melbourne","N03787232767"),
        transactionItemModel(R.drawable.payment_method,"Queens Street - Brooklyn Main Street","5 Mar 2020","-$6.0","NewYork","N03787123e671")
    )

    var itemModelList = ArrayList<transactionItemModel>();
    var itemAdapter : transactionItemAdapter ? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listView = findViewById<ListView>(R.id.card_list_view)

        listView.adapter = MyCustomAdapter(this)



        // transaction list
        for (item in itemListModel){
            itemModelList.add(item)
        }

        transaction_history_list.layoutManager = LinearLayoutManager(this)
        transaction_history_list.setHasFixedSize(true)

        itemAdapter = transactionItemAdapter(this)
        itemAdapter!!.setData(itemModelList)
        transaction_history_list.adapter = itemAdapter
    }

    private  class MyCustomAdapter(context:Context):BaseAdapter(){
        private val mContext:Context

        private val amounts = arrayListOf<Number>(
                25
        )

        init {
           mContext = context
        }
        // responsible for how many rows in my list
        override fun getCount(): Int {
            return amounts.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getItem(position: Int): Any {
            return "test string"
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val layoutInflater = LayoutInflater.from(mContext)
            val card = layoutInflater.inflate(R.layout.card, parent,false)

            val amount = card.findViewById<TextView>(R.id.amount)
            amount.text = "$" + amounts.get(position).toString()
            return card
        }
    }

    override fun ClickTransactionItem(itemModel: transactionItemModel) {
        var itemModel1 = itemModel;
        var name = itemModel1.name;

        startActivity(Intent(this@homeActivity,TransactionDetailsActivity::class.java).putExtra("data",itemModel1))

        //when(name){

            //"apple"->
            //startActivity(Intent(this@homeActivity,TransactionDetailsActivity::class.java).putExtra("data",itemModel1))

            //else ->{
                //Toast.makeText(this@homeActivity,"no action",Toast.LENGTH_LONG).show()
            //}
        //}
    }
}