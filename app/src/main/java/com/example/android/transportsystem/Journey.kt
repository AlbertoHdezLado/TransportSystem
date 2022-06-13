package com.example.android.transportsystem

data class Journey(
    var id: String ?= null,
    var userID: String ?= null,
    var date: String ?= null,
    var kind: String ?= null,
    var initialStation: String ?= null,
    var finalStation: String ?= null,
    var money: Long ?= null,
    var time: Long ?= null) {
}