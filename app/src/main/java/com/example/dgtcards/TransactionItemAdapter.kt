package com.example.dgtcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.transaction_item.view.*


class TransactionItemAdapter(var clickTransactionItem:ClickTransactionItem): RecyclerView.Adapter<TransactionItemAdapter.TransactionAdapterVH>() {

    private var itemModelList = ArrayList<TransactionItemModel>();

    fun setData(itemModelList: ArrayList<TransactionItemModel>){
        this.itemModelList = itemModelList
        notifyDataSetChanged()
    }

    interface ClickTransactionItem{
        fun clickTransactionItem(itemModel: TransactionItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapterVH {
        return TransactionAdapterVH(
                LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemModelList.size
    }

    override fun onBindViewHolder(holder: TransactionAdapterVH, position: Int) {
        var itemModel = itemModelList[position]

        holder.imageView.setImageResource(itemModel.image!!)
        holder.name.text = itemModel.type!!.uppercase() + " - " + itemModel.city
        holder.desc.text = itemModel.type!!.uppercase()

        if(itemModel.type == "recharge"){
            holder.amount.text = "+$" + itemModel.amount
        }else {
            holder.amount.text = "-$" + itemModel.amount
        }

        holder.itemView.setOnClickListener {
            clickTransactionItem.clickTransactionItem(itemModel)
        }
    }

    class TransactionAdapterVH(itemView: View) :RecyclerView.ViewHolder(itemView){
         var imageView = itemView.imageView
         var name = itemView.transaction_title
         var desc = itemView.transaction_dec
         var amount = itemView.transaction_amount
    }

}