package com.example.dgtcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.card.view.*
import java.util.ArrayList

class CardAdapter(var clickCardItem: ClickCardItem): RecyclerView.Adapter<CardAdapter.CardListViewHolder>() {

    private var cardList = ArrayList<CardModel>();

    fun setData(cardList: ArrayList<CardModel>){
        this.cardList = cardList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card,parent,false)
        return CardListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    interface ClickCardItem{
        fun ClickCardItem(itemModel: CardModel)
    }


    override fun onBindViewHolder(holder: CardListViewHolder, position: Int) {

        val currentItem = cardList[position];

        currentItem.image?.let { holder.cityIcon.setImageResource(it) }
        holder.amount.text = "$" + currentItem.balance.toString()
        holder.cityName.text = currentItem.city
        currentItem.cardBackground?.let { holder.cardItem.setBackgroundColor(it) }

        holder.itemView.setOnClickListener {
            clickCardItem.ClickCardItem(currentItem)
        }
    }

    class CardListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var amount = itemView.amount
        var cityName = itemView.cityName
        var cityIcon = itemView.cityIcon
        var cardItem = itemView.card_item
    }

}