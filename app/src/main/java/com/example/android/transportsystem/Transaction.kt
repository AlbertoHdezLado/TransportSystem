package com.example.android.transportsystem

data class Transaction(
    var date: String ?= null,
    var money: Long ?= null,
    var userID: String ?= null,
    var id: String ?= null) {

}