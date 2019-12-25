package com.soda.avalon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_room.*
import kotlinx.android.synthetic.main.activity_waiting_room.*
import kotlinx.android.synthetic.main.search_room_list.*
import kotlinx.android.synthetic.main.search_room_list.view.*

class SearchRoomActivity : AppCompatActivity() {

    private lateinit var adapter: FirestoreRecyclerAdapter<Room,SearchHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_room)




        rc_searchroom.layoutManager = LinearLayoutManager(this)
        rc_searchroom.setHasFixedSize(true)
        val query = FirebaseFirestore.getInstance().collection("room")
            .whereEqualTo("roomStatus",0)
            .whereLessThan("playerCount",5)


        val option = FirestoreRecyclerOptions.Builder<Room>()
            .setQuery(query,Room::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Room,SearchHolder>(option){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
                return SearchHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_room_list,parent,false))
            }

            override fun onBindViewHolder(holder: SearchHolder, position: Int, model: Room) {
                holder.bindSearch(model)
                holder.joinButton.setOnClickListener {
                    currentRoom = model
                    currentRoom.players.add(Player(nickName, currentRoom.playerCount))
                    currentRoom.playerCount = currentRoom.players.size
                    firestore.collection("room").document(currentRoom.roomId).set(currentRoom)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }

        }
        rc_searchroom.adapter = adapter
    }


    inner class SearchHolder (view :View) :RecyclerView.ViewHolder(view){
        val roomHost :TextView = view.tv_roomhost
        val roomNumber :TextView = view.tv_roomnumber
        val joinButton :Button = view.bt_join
        val playerCount :TextView = view.tv_playercount

        fun bindSearch(room:Room){
            roomHost.text = "房主:${room.roomHost}"
            roomNumber.text = "房號:${room.roomId}"
            playerCount.text = "人數:${room.players.size}/5"
        }
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
