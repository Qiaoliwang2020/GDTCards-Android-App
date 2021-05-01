package com.example.dgtcards

import java.io.Serializable

data class transactionItemModel (
    var image:Int,
    var name:String,
    var desc:String,
    var cost:String,
    var city:String,
    var id:String
):Serializable