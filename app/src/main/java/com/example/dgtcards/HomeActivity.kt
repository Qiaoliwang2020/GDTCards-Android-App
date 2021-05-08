package com.example.dgtcards

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(),transactionItemAdapter.ClickTransactionItem,CardAdapter.ClickCardItem  {

    private lateinit var dbRefCards : DatabaseReference
    private lateinit var dbRefUser : DatabaseReference
    private lateinit var auth: FirebaseAuth

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

        getUserData()
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

    private fun getUserData(){
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")
        val user = auth.currentUser
        val userInfo = dbRefUser.child(user.uid)

        userInfo.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,error.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userName.text = "Welcome " + snapshot.child("fullName").value.toString()
            }
        })
    }

    private fun getCardData(){

        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")
        val user = auth.currentUser
        val userCards: Query = dbRefCards.orderByChild("userId").equalTo(user.uid)
        userCards.addValueEventListener(object : ValueEventListener{
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
                else{
                    empty_view.isVisible = true
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createCard(view: View) {

        val dialog = BottomSheetDialog(this)
        val offsetFromTop = 150
        dialog?.behavior?.apply {
            isFitToContents = false
            setExpandedOffset(offsetFromTop)
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        val view = layoutInflater.inflate(R.layout.create_card_layout,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener{
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()

        val submit = view.findViewById<Button>(R.id.submit)

        submit.setOnClickListener{
            dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")

            if(formValidate(view)){
                val userId = auth.currentUser.uid
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
                val id = city + userId
                val cardHolderName = view.findViewById<EditText>(R.id.cardHolderNameInput)!!.text.toString()
                val gender = gender
                val residence = residence
                val email = view.findViewById<EditText>(R.id.EmailInput)!!.text.toString()
                val address = view.findViewById<EditText>(R.id.CurrentAddressInput)!!.text.toString()


                val card = CardModel(userId,image,balance,city,id,expireDay,createdTime,
                        backgroundColor,cardHolderName,gender,residence,address,email)

                dbRefCards.child(city).setValue(card).addOnSuccessListener {

                    view.findViewById<EditText>(R.id.city).text.clear()
                    view.findViewById<EditText>(R.id.cardHolderNameInput).text.clear()
                    view.findViewById<EditText>(R.id.CurrentAddressInput).text.clear()
                    view.findViewById<EditText>(R.id.EmailInput).text.clear()

                    dialog.dismiss()

                    Toast.makeText(this,"item saved",Toast.LENGTH_LONG).show()

                    cardsArrayList.clear()
                    getCardData()

                }.addOnFailureListener {
                    Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                }
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
                        //Toast.makeText(this,gender,Toast.LENGTH_LONG).show()
                    }
                R.id.genderMale ->
                    if (checked) {
                        gender = view.findViewById<RadioButton>(R.id.genderMale).text.toString()
                        //Toast.makeText(this,gender,Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun formValidate(view: View):Boolean{

        val city = view.findViewById<EditText>(R.id.city)
        val cardHolderName = view.findViewById<EditText>(R.id.cardHolderNameInput)
        val currentAddress = view.findViewById<EditText>(R.id.CurrentAddressInput)
        val email = view.findViewById<EditText>(R.id.EmailInput)


        if(city.text.toString().isEmpty()){
            city.error ="city should not be blank"
            return false
        }else if(cardHolderName.text.toString().isEmpty()){
            cardHolderName.error ="cardHolderName should not be blank"
            return false
        }else if(currentAddress.text.toString().isEmpty()){
            currentAddress.error ="currentAddress should not be blank"
            return false
        }else if(email.text.toString().isEmpty()){
            email.error ="email should not be blank"
            return false
        }
        return true
    }

    private fun logout(){
        auth.signOut()
        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        finish()
    }
}


