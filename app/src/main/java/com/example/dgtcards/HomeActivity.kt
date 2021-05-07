package com.example.dgtcards

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(),transactionItemAdapter.ClickTransactionItem,CardAdapter.ClickCardItem  {

    private lateinit var dbref : DatabaseReference
    private lateinit var cardListRecyclerView : RecyclerView
    private lateinit var cardsArrayList : ArrayList<CardModel>
    var cardAdapter : CardAdapter ? = null;
    var residence: Boolean? = null
    var gender :String? = null


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
        cardListRecyclerView = findViewById(R.id.cards)
        cardListRecyclerView.layoutManager = LinearLayoutManager(this)
        cardListRecyclerView.setHasFixedSize(true)

        cardsArrayList = arrayListOf<CardModel>()
        getCardData()

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

    override fun ClickCardItem(itemModel: CardModel) {
        var card = itemModel;
        startActivity(Intent(this@HomeActivity,CardDetailsActivity::class.java).putExtra("data",card))
    }

    private fun getCardData(){
        dbref = FirebaseDatabase.getInstance().getReference("Cards")
        dbref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,error.toString(),Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(cardSnapshot in snapshot.children){
                        var card = cardSnapshot.getValue(CardModel::class.java)
                        cardsArrayList.add(card!!)
                    }

                    cardAdapter = CardAdapter(this@HomeActivity)
                    cardAdapter!!.setData(cardsArrayList)

                    cardListRecyclerView.adapter = cardAdapter
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            dbref = FirebaseDatabase.getInstance().getReference("Cards")

            val current = LocalDateTime.now()
            val nextYear = current.plusYears(1)
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            val rnd = Random()
            val backgroundColor: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            val expireDay =nextYear.format(dateFormatter)
            val createdTime = current.format(dateFormatter)
            val image = R.drawable.public_transport
            val balance = 0.00
            val city = view.findViewById<EditText>(R.id.city)!!.text.toString()
            val id = dbref.key.toString()
            val cardHolderName = view.findViewById<EditText>(R.id.cardHolderNameInput)!!.text.toString()
            val gender = gender
            val residence = residence
            val email = view.findViewById<EditText>(R.id.EmailInput)!!.text.toString()
            val address = view.findViewById<EditText>(R.id.CurrentAddressInput)!!.text.toString()


            // dialog.dismiss()

            val card = CardModel(image,balance,city,id,expireDay,createdTime,
                backgroundColor,cardHolderName,gender,residence,address,email)

            dbref.child(city).setValue(card).addOnSuccessListener {

                view.findViewById<EditText>(R.id.city).text.clear()
                view.findViewById<EditText>(R.id.cardHolderNameInput).text.clear()
                view.findViewById<EditText>(R.id.CurrentAddressInput).text.clear()
                view.findViewById<EditText>(R.id.EmailInput).text.clear()

                dialog.dismiss()

                Toast.makeText(this,"item saved",Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
        }
    }


    fun onRadioResidenceClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.isResidence ->
                    if (checked) {
                        residence = true

                    }
                R.id.notResidence ->
                    if (checked) {
                        residence = false
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


