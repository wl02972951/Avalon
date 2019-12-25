package com.soda.avalon

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room(
    val roomId : String,
    val roomHost : String,
    var players : MutableList<Player>,
    var playerCount : Int,
    var roomStatus : Int,
    var GoodScore: Int,
    var BadScore :Int,
    var votingRound : Int
) :Parcelable{
    constructor() : this("","",mutableListOf<Player>(),0,0 ,0,0,0)
    constructor(roomId: String,roomHost: String) : this(roomId,roomHost, mutableListOf<Player>(Player(roomHost,0)),1,0,0,0,0)
}
