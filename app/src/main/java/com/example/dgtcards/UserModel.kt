package com.example.dgtcards
import java.io.Serializable

data class UserModel (
        var id:String? = null,
        var fullName:String ? = null,
        var email: String? ? =null,
        var phone :String ? = null,
        var password:String ? = null
):Serializable