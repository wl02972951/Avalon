package com.soda.avalon

import com.google.firebase.firestore.FirebaseFirestore

val ROOM_ID = "room_id"
val RC_ROLE = 204
val RC_SEARCHROOM =203
val RC_WAITROOM =201
val RC_NICK_NAME = 202
val RC_LOGIN = 199
val RC_GAME = 200
val firestore = FirebaseFirestore.getInstance()
var nickName = ""
var currentPlayer :Player? = null
var currentRoom :Room = Room()
