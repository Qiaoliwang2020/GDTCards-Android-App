package com.example.dgtcards

import java.io.Serializable

class cardItemModel (
    var image:Int,
    var balance:Double ? = 0.00,
    var city:String,
    var id:String,
    var expireDay:String,
    var createTime:String,
    var cardBackground:Int,
    var cardHoldderName:String ? = null,
    var gender:String ? = null,
    var recidence :Boolean ? = false,
    var currentAddress:String ? = null,
    var email:String ? = null
):Serializable