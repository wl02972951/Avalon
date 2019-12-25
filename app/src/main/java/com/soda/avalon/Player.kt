package com.soda.avalon

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player(
    val nickName: String,
    var role: Int?,
    var joinIndex: Int?,
    var vote :Boolean?):Parcelable{
    constructor():this("",null,null,null)
    constructor(nickName: String,joinIndex: Int?):this(nickName,null,joinIndex,null)
}