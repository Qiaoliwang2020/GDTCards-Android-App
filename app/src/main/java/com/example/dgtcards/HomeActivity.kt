package com.example.dgtcards

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtcards.databinding.ChangePasswordLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.change_password_layout.*
import kotlinx.android.synthetic.main.user_profile_layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(),TransactionItemAdapter.ClickTransactionItem,CardAdapter.ClickCardItem  {

    private lateinit var dbRefCards : DatabaseReference
    private lateinit var dbRefUser : DatabaseReference
    private lateinit var dbRefPayment : DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var cardListRecyclerView : RecyclerView
    private lateinit var cardsArrayList : ArrayList<CardModel>
    var cardAdapter : CardAdapter ? = null

    var residence: Boolean? = null
    var gender :String? = null
    var fullName :String ? = null
    var email:String ? = null
    var phone :String ? = null

    private lateinit var transactionsRecyclerView : RecyclerView
    private var transactionItemModelList = ArrayList<TransactionItemModel>()
    var transactionItemAdapter : TransactionItemAdapter ? = null

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
        transactionsRecyclerView = findViewById(R.id.transaction_history_list)
        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionsRecyclerView.setHasFixedSize(true)

        transactionItemModelList = arrayListOf<TransactionItemModel>()
        getPaymentsData()
    }


    override fun clickTransactionItem(itemModel: TransactionItemModel) {

        var transactionItem = itemModel
        startActivity(Intent(this@HomeActivity,TransactionDetailsActivity::class.java).putExtra("data",transactionItem))
    }

    override fun ClickCardItem(itemModel: CardModel) {
        var card = itemModel
        startActivity(Intent(this@HomeActivity,CardDetailsActivity::class.java).putExtra("data",card))
    }

    private fun getUserData(){
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")
        val user = auth.currentUser
        val userInfo = dbRefUser.child(user!!.uid)

        userInfo.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,error.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userName.text = "Welcome " + snapshot.child("fullName").value.toString()
                fullName = snapshot.child("fullName").value.toString()
                email = snapshot.child("email").value.toString()
                phone = snapshot.child("phone").value.toString()
            }
        })
    }

    private fun getCardData(){

        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")
        val user = auth.currentUser
        val userCards: Query = dbRefCards.orderByChild("userId").equalTo(user!!.uid)
        userCards.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,error.toString(),Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(cardSnapshot in snapshot.children){
                        var card = cardSnapshot.getValue(CardModel::class.java)
                        if (card != null) {
                            card.image = R.drawable.public_transport
                        };
                        cardsArrayList.add(card!!)
                    }

                    cardAdapter = CardAdapter(this@HomeActivity)
                    cardAdapter!!.setData(cardsArrayList)

                    cardListRecyclerView.adapter = cardAdapter
                    empty_view.isVisible = false
                }
                else{
                    empty_view.isVisible = true
                }
            }

        })
    }

    private fun getPaymentsData(){

        dbRefPayment = FirebaseDatabase.getInstance().getReference("Payments")
        val user = auth.currentUser
        val userPayments: Query = dbRefPayment.orderByChild("userId").equalTo(user!!.uid)
        userPayments.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,error.toString(),Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(paymentSnapshot in snapshot.children){
                        var payment = paymentSnapshot.getValue(TransactionItemModel::class.java)
                        if (payment != null) {
                            transactionItemModelList.add(payment)
                        }
                    }

                    transactionItemAdapter = TransactionItemAdapter(this@HomeActivity)
                    transactionItemAdapter!!.setData(transactionItemModelList)

                    transactionsRecyclerView.adapter = transactionItemAdapter
                }
                else{
                    // empty_view.isVisible = true
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createCard(view: View) {

        val dialog = BottomSheetDialog(this)
        val offsetFromTop = 150
        dialog.behavior.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop
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
                val userId = auth.currentUser!!.uid
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

                dbRefCards.child(id).setValue(card).addOnSuccessListener {

                    view.findViewById<EditText>(R.id.city).text.clear()
                    view.findViewById<EditText>(R.id.cardHolderNameInput).text.clear()
                    view.findViewById<EditText>(R.id.CurrentAddressInput).text.clear()
                    view.findViewById<EditText>(R.id.EmailInput).text.clear()

                    dialog.dismiss()

                    Toast.makeText(this,"item saved",Toast.LENGTH_SHORT).show()

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

    fun showUserProfile(view: View){
        val profileDialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.user_profile_layout,null)

        val close = view.findViewById<ImageView>(R.id.profile_close)

        close.setOnClickListener{
            profileDialog.dismiss()
        }

        view.findViewById<EditText>(R.id.profile_fullName).setText(fullName)
        view.findViewById<EditText>(R.id.profile_email).setText(email)
        view.findViewById<EditText>(R.id.profile_phone).setText(phone)

        profileDialog.setContentView(view)
        profileDialog.show()

        val update = view.findViewById<Button>(R.id.Update)

        update.setOnClickListener{
            changeUserInfo(view)
            profileDialog.dismiss()
        }

        val changePwd = view.findViewById<TextView>(R.id.changePassword)

        changePwd.setOnClickListener{
            profileDialog.dismiss()
            showChangePwdDialog()
        }
    }
    // show change password dialog
    private fun showChangePwdDialog() {
        val dialogBinding: ChangePasswordLayoutBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.change_password_layout,
                null,
                false
            )

        val customDialog = AlertDialog.Builder(this, 0).create()

        customDialog.apply {
            setView(dialogBinding?.root)
            setCancelable(false)
        }.show()

        dialogBinding?.noticeClose?.setOnClickListener{
            customDialog.dismiss()
        }
        dialogBinding?.btnOk?.setOnClickListener {
            val currentPwd = dialogBinding.currentPasswordInput.text.toString()
            val newPwd = dialogBinding.newPasswordInput.text.toString()
            val confirmPwd = dialogBinding.confirmPasswordInput.text.toString()
            if(newPwd == confirmPwd){
                changePassword(currentPwd,newPwd)
            }else{
                Toast.makeText(this,"Password mismatching.",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun changeUserInfo(view:View){
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")
        val user = auth.currentUser

        val newFullName = view.findViewById<EditText>(R.id.profile_fullName).text.toString()
        val newPhoneNumber = view.findViewById<EditText>(R.id.profile_phone).text.toString()

        if(user != null && user.email != null){
            dbRefUser.child(user.uid).child("fullName").setValue(newFullName).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"full Name change successfully",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"full Name change failed",Toast.LENGTH_LONG).show()
                }
            }
            dbRefUser.child(user.uid).child("phone").setValue(newPhoneNumber).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Phone number change successfully",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Phone number change failed",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
    private fun changePassword(currentPwd:String,newPwd:String){
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")
        val user = auth.currentUser
        if(user != null && user.email != null){
            val credential = EmailAuthProvider.getCredential(user.email!!,currentPwd)
            user.reauthenticate(credential).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Re-authenticated success",Toast.LENGTH_LONG).show()
                    user.updatePassword(newPwd).addOnCompleteListener{ task->
                        if(task.isSuccessful){
                            dbRefUser.child(user.uid).child("password").setValue(newPwd)
                            Toast.makeText(this,"Password changed successfully",Toast.LENGTH_LONG).show()
                            auth.signOut()
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        }
                    }
                } else{
                    Toast.makeText(this,"Re-authenticated failed",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    fun logout(view:View){
        auth.signOut()
        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        finish()
    }
}



