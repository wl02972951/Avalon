package com.soda.avalon

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_waiting_room.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.random.Random

class WaitingRoomActivity : AppCompatActivity() ,AnkoLogger{
    lateinit var roomID :String
    lateinit var roomRef :DocumentReference

    private val TAG = WaitingRoomActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)


        roomID =  currentRoom.roomId
        roomRef = firestore.collection("room").document(roomID)

        bt_waiting_start.setOnClickListener {
            if(currentRoom.players.size<1){
                Toast.makeText(this,"人數不足無法開始",Toast.LENGTH_LONG).show()
            } else if (nickName !=currentRoom.roomHost){
                Toast.makeText(this,"您並非房主",Toast.LENGTH_LONG).show()
            }else{
                setPlayersRole()
                currentRoom.roomStatus = 1
                roomRef.set(currentRoom)
            }

        }

        roomRef.addSnapshotListener{snapshot, e->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(e.message)
                    .setPositiveButton("OK"){ dialog, which ->
                        finish()
                    }
                finish()
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                currentRoom = snapshot!!.toObject(Room::class.java)!!
                if(currentRoom.roomStatus==1){
                    info { "Start Game" }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                bindView(currentRoom)
            } else {
                Log.d(TAG, "Current data: null")
            }

        }


    }

    fun bindView(room: Room){
        tv_waitroom_roomnumber.text = "房號${currentRoom.roomId}"
        var players = mutableListOf<String>()
        room.players.forEach {
            players.add(it.nickName)
        }
        val listAdapter =  ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,players)
        list_player.adapter = listAdapter
        if (players.size>=5){
            bt_waiting_start.text = "等待房主開始遊戲"
        }else{
            bt_waiting_start.text = "等待玩家\n人數:${players.size}/5"
        }

    }

    private fun setPlayersRole() {
        var i = 0
        val roleList = getRandomRole(currentRoom.playerCount)
        currentRoom.players.forEach {
            it.role = roleList[i]
            i++
        }
    }

    private fun getRandomRole(playerCount :Int) :List<Int>{
        val roleSet = mutableSetOf<Int>()

        while (roleSet.size<playerCount){
            roleSet.add(Random.nextInt(playerCount))
        }
        return roleSet.toList()
    }


    override fun onDestroy() {
        super.onDestroy()
        if(currentRoom.roomStatus!=0){

        }else if (currentRoom.roomHost == nickName) {
            roomRef.delete()
        }else{
            var deleteIndex : Int? = null
            var i = 0
            currentRoom.players.forEach {
                if (it.nickName == nickName){
                    deleteIndex = it.joinIndex
                }
            }
            deleteIndex?.let {
                currentRoom.players.removeAt(it)
                currentRoom.playerCount = currentRoom.players.size
            }
            roomRef.set(currentRoom)

        }
    }
}
