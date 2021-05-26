package com.example.dgtcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.empty_transactions
import kotlinx.android.synthetic.main.activity_wallets.*

class Wallets : AppCompatActivity(),TransactionItemAdapter.ClickTransactionItem {

    private lateinit var dbRefPayment : DatabaseReference
    private lateinit var auth: FirebaseAuth

    // initialize carousel array and view
    var imageArray: ArrayList<Int> = ArrayList()
    var carouselView:CarouselView? = null

    // Initialize transaction list view and array and adpter to show transaction list
    private lateinit var transactionsRecyclerView : RecyclerView
    private var transactionItemModelList = ArrayList<TransactionItemModel>()
    var transactionItemAdapter : TransactionItemAdapter ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)

        // add image to image array
        imageArray.add(R.drawable.adv1)
        imageArray.add(R.drawable.adv2)
        imageArray.add(R.drawable.adv3)
        imageArray.add(R.drawable.adv4)

        carouselView = findViewById(R.id.carouselView)
        carouselView!!.pageCount = imageArray.size

        carouselView!!.setImageListener(imageListener)


        // initialize auth
        auth = FirebaseAuth.getInstance()

        // show transaction list
        transactionsRecyclerView = findViewById(R.id.transaction_history_list)
        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionsRecyclerView.setHasFixedSize(true)

        transactionItemModelList = arrayListOf<TransactionItemModel>()
        getPaymentsData()


        //initialize array for line chart
        val entries = ArrayList<Entry>()

       //add entry data to arrays
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))


        // set the label of line chart
        val vl = LineDataSet(entries, "My Transaction")

        // default setting of line chart
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.green_700
        vl.fillAlpha = R.color.black

        // set xAxis of line chart
        lineChart.xAxis.labelRotationAngle = 0f

        // set the data to line chart
        lineChart.data = LineData(vl)
    }

    // image listener for carousel
    var imageListener = ImageListener{ position, imageView ->imageView.setImageResource(imageArray[position])}


    // click transaction list item to check transaction details
    override fun clickTransactionItem(itemModel: TransactionItemModel) {
        var transactionItem = itemModel
        startActivity(Intent(this@Wallets,TransactionDetailsActivity::class.java).putExtra("data",transactionItem))
    }

    // get all payments data by user id
    private fun getPaymentsData(){

        // access to firebase Payments table
        dbRefPayment = FirebaseDatabase.getInstance().getReference("Payments")
        val user = auth.currentUser

        // query payments data by user id
        val userPayments: Query = dbRefPayment.orderByChild("userId").equalTo(user!!.uid);
        userPayments.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Wallets,error.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(paymentSnapshot in snapshot.children){
                        var payment = paymentSnapshot.getValue(TransactionItemModel::class.java)
                        if (payment != null) {
                            transactionItemModelList.add(payment)
                        }
                    }

                    transactionItemAdapter = TransactionItemAdapter(this@Wallets)
                    transactionItemAdapter!!.setData(transactionItemModelList)

                    transactionsRecyclerView.adapter = transactionItemAdapter
                    empty_transactions.isVisible = false
                }
                else{
                    // if no data of payments history
                    empty_transactions.isVisible = true
                }
            }

        })
    }
    // back to home page
    fun navigateHome(view: View) {
        val intent = Intent(this, HomeActivity::class.java).apply {}
        startActivity(intent)
    }
}


