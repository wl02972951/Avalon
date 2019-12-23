package com.soda.avalon

data class Users(
    val uid :String,
    val email :String,
    val nickname:String
){
    constructor() : this("","","")
}