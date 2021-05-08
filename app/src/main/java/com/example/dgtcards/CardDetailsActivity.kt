package com.example.dgtcards

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.card.*

class CardDetailsActivity : AppCompatActivity() {

    var itemModel:CardModel ? = null;

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
}