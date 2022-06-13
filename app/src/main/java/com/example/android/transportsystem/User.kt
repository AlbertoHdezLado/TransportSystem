package com.example.android.shelted.Classes

data class User(
    var name: String ?= null,
    var surname: String ?= null,
    var birthdate: String ?= null,
    var email: String ?= null,
    var pass:String ?= null,
    var money: Int = 0) {
}