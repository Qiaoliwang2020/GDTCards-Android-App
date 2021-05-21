package com.example.dgtcards

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.recharge.*
import java.util.*
import kotlin.concurrent.schedule

class CardDetailsActivity : AppCompatActivity() {

    var itemModel:CardModel ? = null;
    var plan:String ? = null;
    private lateinit var dbRefCards : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        itemModel = intent.getSerializableExtra("data") as CardModel;

        itemModel!!.image?.let { city_icon.setImageResource(it) }
        city_name.text = itemModel!!.city
        balance_amount.text = "$"+ itemModel!!.balance.toString()
        card_id.text = itemModel!!.id
        expire_day.text = "Expire: " + itemModel!!.expireDay
        created_time.text = "Created Time: "+ itemModel!!.createTime
        itemModel!!.cardBackground?.let { card_background.setBackgroundColor(it) }
        page_title.text = itemModel!!.city


        itemModel!!.id?.let { displayBitmap(it) }
    }

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

    fun navigateHome(view: View){
        val intent = Intent(this, HomeActivity::class.java).apply {}
        startActivity(intent)
    }

    fun removeCard(view: View){
        dbRefCards = FirebaseDatabase.getInstance().getReference("Cards");
        val id = itemModel!!.id.toString();
        dbRefCards.child(id).removeValue();
        Toast.makeText(this,"item Removed", Toast.LENGTH_LONG).show()
        Timer().schedule(3000){
            navigateHome(view);
        }
    }
    fun recharge(view: View){
        val rechargeDialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.recharge,null)

        val close = view.findViewById<ImageView>(R.id.iv_close)

        close.setOnClickListener{
            rechargeDialog.dismiss()
        }

        rechargeDialog.setContentView(view)
        rechargeDialog.show()
    }

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
}

