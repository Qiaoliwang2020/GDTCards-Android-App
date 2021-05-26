package com.example.dgtcards

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import kotlinx.android.synthetic.main.activity_card_details.*
import java.util.*
import kotlin.concurrent.schedule

class CardDetailsActivity : AppCompatActivity() {

    // initialize card data model and plan(recharge amount)
    var itemModel:CardModel ? = null
    // plan (recharge amount)
    var plan:String ? = null

    // initialize auth and database reference
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRefCards : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        // access auth
        auth = FirebaseAuth.getInstance()

        // get the data from homeActivity
        itemModel = intent.getSerializableExtra("data") as CardModel

        // show data to page
        itemModel!!.image?.let { city_icon.setImageResource(it) }
        city_name.text = itemModel!!.city
        balance_amount.text = "$"+ itemModel!!.balance.toString()
        card_id.text = itemModel!!.id
        expire_day.text = "Expire: " + itemModel!!.expireDay
        created_time.text = "Created Time: "+ itemModel!!.createTime
        itemModel!!.cardBackground?.let { card_background.setBackgroundColor(it) }
        page_title.text = itemModel!!.city

        // show remove card button or withdraw amount button according to balance
        if(itemModel!!.balance!! > 0){
            remove_Card.isVisible = false;
            withdraw_amount.isVisible = true;
        }else{
            remove_Card.isVisible = true;
            withdraw_amount.isVisible = false;
        }

        // set barcode by card id
        itemModel!!.id?.let { displayBitmap(it) }
    }

    // create barcode bitmap
    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }

    // show barcode bitmap
    private fun displayBitmap(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)

        image_barcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = ContextCompat.getColor(baseContext,R.color.black),
                backgroundColor = ContextCompat.getColor(baseContext,android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
    }

    // back to home page
    fun navigateHome(view: View){
        val intent = Intent(this, HomeActivity::class.java).apply {}
        startActivity(intent)
    }

    // when balance is 0, user can click to remove the card
    fun removeCard(view: View){
        // access firebase database table Cards
        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards")

        // remove the card by id
        val id = itemModel!!.id.toString()
        dbRefCards.child(id).removeValue().addOnSuccessListener {
            // if success, show success message and return to home page after 2 second
            Toast.makeText(this,"card Removed", Toast.LENGTH_LONG).show()
            Timer().schedule(2000){
                navigateHome(view)
            }
        }.addOnFailureListener {
            // if failed show message
            Toast.makeText(this,"card remove failed", Toast.LENGTH_LONG).show()
        }

    }
    // when user click recharge
    fun recharge(view: View){

        // create a bottom sheet dialog and set the distance and behavior
        val rechargeDialog = BottomSheetDialog(this)
        val offsetFromTop = 650
        rechargeDialog.behavior.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        // set recharge layout as the dialog view
        val view = layoutInflater.inflate(R.layout.recharge,null)

        // get close button
        val close = view.findViewById<ImageView>(R.id.iv_close)

        // when click close button, close the dialog
        close.setOnClickListener{
            rechargeDialog.dismiss()
        }
        // set content view and show the dialog
        rechargeDialog.setContentView(view)
        rechargeDialog.show()

        // when user click next, get next button
        val next = view.findViewById<Button>(R.id.next);
        next.setOnClickListener{
            // get current user
            val user = auth.currentUser;

            // validate the recharge form
            if(paymentFormValidate(view)){

                // set the payment data
                val type = "recharge"
                val paymentData = PaymentInfo();
                paymentData.amount = plan;
                paymentData.cardHolderName = view.findViewById<EditText>(R.id.payCardHolderNameInput).text.toString();
                paymentData.userId = user!!.uid;
                paymentData.cardId = itemModel!!.id.toString();
                paymentData.type = type;
                paymentData.city = itemModel!!.city.toString();
                paymentData.cardBalance = itemModel!!.balance;

                // navigate to confirm payment and send the paymentInfo data to confirmPayment
                val intent = Intent(this, ConfirmPayment::class.java).apply {}
                intent.putExtra("extra_paymentInfo",paymentData)
                startActivity(intent)
            }
        }
    }
    // validate for payment form
    private fun paymentFormValidate(view: View):Boolean{

        val cardholderName = view.findViewById<EditText>(R.id.payCardHolderNameInput);
        val accountNumber = view.findViewById<EditText>(R.id.cardNumberEditText);
        val cardDate = view.findViewById<EditText>(R.id.cardDateEditText);
        val cardCVC = view.findViewById<EditText>(R.id.cardCVCEditText);
        val recharge100 = view.findViewById<RadioButton>(R.id.recharge100);

        if(cardholderName.text.toString().isEmpty()){
            cardholderName.error = "Your card holder's name should not be blank"
            return false
        }else if(accountNumber.text.toString().isEmpty()){
            accountNumber.error = "account number should not be blank"
            return false
        }else if(cardDate.text.toString().isEmpty()){
            cardDate.error = "card expire Date should not be blank"
            return false
        }else if (cardCVC.text.toString().isEmpty()){
            cardCVC.error = "CVC should not be blank"
            return false
        }else if(plan == null){
            recharge100.error = "Please select a plan"
            return false
        }

        return true
    }
    // when plan change, get the plan(recharge amount)
    fun onRadioPlanClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {

                R.id.recharge10 ->
                    if (checked) {
                        plan = view.findViewById<RadioButton>(R.id.recharge10).text.toString()
                    }
                R.id.recharge20 ->
                    if (checked) {
                        plan = view.findViewById<RadioButton>(R.id.recharge20).text.toString()
                    }
                R.id.recharge30 ->
                    if (checked) {
                        plan = view.findViewById<RadioButton>(R.id.recharge30).text.toString()
                    }
                R.id.recharge50 ->
                    if (checked) {
                        plan = view.findViewById<RadioButton>(R.id.recharge50).text.toString()
                    }
                R.id.recharge100 ->
                    if (checked) {
                        plan = view.findViewById<RadioButton>(R.id.recharge100).text.toString()
                    }
            }
        }
    }

    // when user click to withdraw
    fun withdrawAmount(view: View) {

        // create a bottom sheet
        val withdrawDialog = BottomSheetDialog(this)

        // set the distance and behavior of the bottom sheet
        val offsetFromTop = 50
        withdrawDialog.behavior.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        // set the withdraw layout as the view of this dialog
        val view = layoutInflater.inflate(R.layout.withdraw,null)

        // get close button
        val close = view.findViewById<ImageView>(R.id.iv_close)

        // click to close the dialog
        close.setOnClickListener{
            withdrawDialog.dismiss()
        }

        // set the content view and show dialog
        withdrawDialog.setContentView(view)
        withdrawDialog.show()

        // set the default currencies
        val currencies = resources.getStringArray(R.array.currencies)
        // get the spinner of currenies
        val spinner = view.findViewById<Spinner>(R.id.currency_spinner)
        // set the item adapter
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, currencies
            )
            spinner.adapter = adapter
        }
        // get withdraw all button
        val withdrawAll = view.findViewById<TextView>(R.id.withdraw_all)

        // when user click withdraw all set the card balance as withdraw amount
        withdrawAll.setOnClickListener{
            val amountInput = view.findViewById<EditText>(R.id.withdraw_numberInput)
            amountInput.setText(itemModel!!.balance.toString())
        }

        // get next button
        val next = view.findViewById<Button>(R.id.next);
        // when user click next
        next.setOnClickListener{
            // get current user
            val user = auth.currentUser;

            // validate the form and set data
            if(withdrawFormValidate(view)){
                val type = "withdraw"
                val paymentData = PaymentInfo();
                paymentData.amount = view.findViewById<EditText>(R.id.withdraw_numberInput).text.toString();
                paymentData.cardHolderName = view.findViewById<EditText>(R.id.payCardHolderNameInput).text.toString();
                paymentData.userId = user!!.uid;
                paymentData.cardId = itemModel!!.id.toString();
                paymentData.type = type;
                paymentData.city = itemModel!!.city.toString();
                paymentData.cardBalance = itemModel!!.balance;

                // navigate to confirm payment and send the paymentInfo data to confirmPayment activity
                val intent = Intent(this, ConfirmPayment::class.java).apply {}
                intent.putExtra("extra_paymentInfo",paymentData)
                startActivity(intent)
            }
        }
    }

    // valiate withdraw form
    private fun withdrawFormValidate(view: View):Boolean{

        val cardholderName = view.findViewById<EditText>(R.id.payCardHolderNameInput);
        val amount = view.findViewById<EditText>(R.id.withdraw_numberInput);
        val bsb = view.findViewById<EditText>(R.id.cardBSB);
        val cardAccount = view.findViewById<EditText>(R.id.cardAccountNumber);

        if(cardholderName.text.toString().isEmpty()){
            cardholderName.error = "Your card holder's name should not be blank"
            return false
        }else if(cardAccount.text.toString().isEmpty()){
            cardAccount.error = "account number should not be blank"
            return false
        }else if (bsb.text.toString().isEmpty()){
            bsb.error = "bsb should not be blank"
            return false
        }else if(amount.text.toString().isEmpty()){
            amount.error = "amount should not be blank"
            return false
        }

        return true
    }
}

