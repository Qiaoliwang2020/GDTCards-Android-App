package com.example.dgtcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.transaction_item.view.*


class transactionItemAdapter(var clickTransactionItem:ClickTransactionItem): RecyclerView.Adapter<transactionItemAdapter.transactionAdapterVH>() {

    var itemModelList = ArrayList<transactionItemModel>();

    fun setData(itemModelList: ArrayList<transactionItemModel>){
        this.itemModelList = itemModelList
        notifyDataSetChanged()
    }

    interface ClickTransactionItem{
        fun ClickTransactionItem(itemModel: transactionItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transactionAdapterVH {
        return transactionAdapterVH(
                LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemModelList.size
    }

    override fun onBindViewHolder(holder: transactionAdapterVH, position: Int) {
        var itemModel = itemModelList[position]

        holder.imageView.setImageResource(itemModel.image)
        holder.name.text = itemModel.name
        holder.desc.text = itemModel.desc
        holder.amount.text = itemModel.cost

        holder.itemView.setOnClickListener {
            clickTransactionItem.ClickTransactionItem(itemModel)
        }
    }

    class transactionAdapterVH(itemView: View) :RecyclerView.ViewHolder(itemView){
         var imageView = itemView.imageView
         var name = itemView.transaction_title
         var desc = itemView.transaction_dec
         var amount = itemView.transaction_amount
    }

}