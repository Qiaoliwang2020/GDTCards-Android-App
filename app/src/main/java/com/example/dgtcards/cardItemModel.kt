package com.example.dgtcards

import java.io.Serializable

data class cardItemModel (
    var image:Int,
    var balance:String,
    var city:String,
    var id:String,
    var expireDay:String,
    var createTime:String,
    var cardBackground:Int
):Serializable