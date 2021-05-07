package com.example.dgtcards

data class CardModel (
    var image:Int ? = null,
    var balance:Double ? = 0.00,
    var city:String ? = null,
    var id:String? = null,
    var expireDay:String ? = null,
    var createTime:String ? = null,
    var cardBackground:Int ? = null,
    var cardHoldderName:String ? = null,
    var gender:String ? = null,
    var recidence :Boolean ? = false,
    var currentAddress:String ? = null,
    var email:String ? = null
)