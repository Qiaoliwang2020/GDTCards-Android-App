package com.example.dgtcards

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView


class homeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listView = findViewById<ListView>(R.id.card_list_view)

        listView.adapter = MyCustomAdapter(this)
    }

    private  class MyCustomAdapter(context:Context):BaseAdapter(){
        private val mContext:Context

        private val amounts = arrayListOf<Number>(
                25,20,10
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




}