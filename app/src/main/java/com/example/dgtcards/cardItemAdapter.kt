package com.example.dgtcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card.view.*

class cardItemAdapter(var clickCardItem:ClickCardItem): RecyclerView.Adapter<cardItemAdapter.cardAdapterVH>() {

    var itemModelList = ArrayList<cardItemModel>();

    fun setData(itemModelList: ArrayList<cardItemModel>){
        this.itemModelList = itemModelList
        notifyDataSetChanged()
    }

    interface ClickCardItem{
        fun ClickCardItem(itemModel: cardItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardAdapterVH {
        return cardAdapterVH(
                LayoutInflater.from(parent.context).inflate(R.layout.card,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemModelList.size
    }

    override fun onBindViewHolder(holder: cardAdapterVH, position: Int) {
        var itemModel = itemModelList[position]

        holder.cityIcon.setImageResource(itemModel.image)
        holder.amount.text = itemModel.balance
        holder.cityName.text = itemModel.city
        holder.cardItem.setBackgroundColor(itemModel.cardBackground)

        holder.itemView.setOnClickListener {
            clickCardItem.ClickCardItem(itemModel)
        }
    }

    class cardAdapterVH(itemView: View) :RecyclerView.ViewHolder(itemView){
         var amount = itemView.amount
         var cityName = itemView.cityName
         var cityIcon = itemView.cityIcon
         var cardItem = itemView.card_item
    }

}