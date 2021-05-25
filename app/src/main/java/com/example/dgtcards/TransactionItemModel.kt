package com.example.dgtcards

import java.io.Serializable

data class TransactionItemModel (
    var image:Int? = R.drawable.payment_method,
    var userId:String ? = null,
    var cardId:String ? = null,
    var amount:String ? = null,
    var cardHolderName:String ? = null,
    var city :String ? = null,
    var type :String ? = null,
    var createTime:String ? = null,
    var notes :String ? = null,
    var reciptNumber :String ? = null,
    var cardBalance :Double ? = null,
):Serializable