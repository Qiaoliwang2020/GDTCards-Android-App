package com.example.dgtcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.create_card_layout.*


class HomeActivity : AppCompatActivity(),transactionItemAdapter.ClickTransactionItem,cardItemAdapter.ClickCardItem {

    var recidence: Boolean? = null
    var gender :String? = null

    // card list
    var cardListModel = arrayOf(
            cardItemModel(R.drawable.melbourne,25.00,"Melbourne","N03787437670","22/04/2022","22/04/2021",R.color.design_default_color_primary),
            cardItemModel(R.drawable.sydney_opera_house,20.00,"Sydney","Sy03787437670","22/04/2022","22/04/2022",R.color.teal_200),
            cardItemModel(R.drawable.new_york,45.00,"New York","NYC03787437670","22/04/2022","22/04/2022",R.color.green_700)
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
        startActivity(Intent(this@HomeActivity,TransactionDetailsActivity::class.java).putExtra("data",transactionItem))
    }

    override fun ClickCardItem(itemModel: cardItemModel) {
        var cardItem = itemModel;
        startActivity(Intent(this@HomeActivity,cardDetailsActivity::class.java).putExtra("data",cardItem))
    }

    fun createCard(view: View) {

        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.create_card_layout,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener{
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()

        val submit = view.findViewById<Button>(R.id.submit)

        submit.setOnClickListener{



//            var city = view.findViewById<EditText>(R.id.city)!!.text
//            var cardHolderName = view.findViewById<EditText>(R.id.cardHolderNameInput)!!.text
//            var email = view.findViewById<EditText>(R.id.emailInput)!!.text
//            var address = view.findViewById<EditText>(R.id.CurrentAddressInput)!!.text
//


            // dialog.dismiss()

            Toast.makeText(this,"item saved",Toast.LENGTH_LONG).show()
        }
    }

    fun onRadioRecidenceClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.isRecidence ->
                    if (checked) {
                        recidence = true

                    }
                R.id.notRecidence ->
                    if (checked) {
                        recidence = false
                    }
            }
        }
    }
    fun onRadioGenderClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {

                R.id.genderFemale ->
                    if (checked) {
                        gender = view.findViewById<RadioButton>(R.id.genderFemale).text.toString()
                        Toast.makeText(this,gender,Toast.LENGTH_LONG).show()
                    }
                R.id.genderMale ->
                    if (checked) {
                        gender = view.findViewById<RadioButton>(R.id.genderMale).text.toString()
                        Toast.makeText(this,gender,Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}