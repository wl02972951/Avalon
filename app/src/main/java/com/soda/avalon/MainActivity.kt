package com.soda.avalon

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.random.Random




class MainActivity : AppCompatActivity() , FirebaseAuth.AuthStateListener ,AnkoLogger{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val user =  FirebaseAuth.getInstance()


        onAuthStateChanged(user)
        bt_newgame.setOnClickListener {
            createNewGame()
        }
        bt_search.setOnClickListener {
            searchRoom()
        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "create your own function", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            startActivity(Intent(this,RoleActivity::class.java))
        }
    }

    private fun searchRoom() {
        if (nickName == "") {
            AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("請先登入帳號")
                .setPositiveButton("進行登入") { dialog, which ->
                    startActivityForResult(Intent(this, LogInActivity::class.java), RC_LOGIN)
                }
                .show()
        } else {

            startActivityForResult(Intent(this, SearchRoomActivity::class.java), RC_SEARCHROOM)
        }
    }

    private fun createNewGame() {
        if (nickName == "") {  //確認是否有登入
            AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("請先登入帳號")
                .setPositiveButton("進行登入") { dialog, which ->
                    startActivityForResult(Intent(this, LogInActivity::class.java), RC_LOGIN)
                }
                .show()
        } else {
            val roomId = getRoomID()
            val newRoom = Room(roomId, nickName)
            firestore.collection("room").document(roomId).set(newRoom)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val newGameIntent = Intent(this, WaitingRoomActivity::class.java)
                        currentRoom = newRoom
                        startActivityForResult(newGameIntent, RC_WAITROOM)
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("錯誤")
                            .setMessage(it.exception?.message)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_login->{
                val loginIntent = Intent(this@MainActivity,LogInActivity::class.java)
                startActivityForResult(loginIntent,RC_LOGIN)
                return true
            }
            R.id.action_nick_name->{
                val nickIntent = Intent(this@MainActivity,NickNameActivity::class.java)
                startActivityForResult(nickIntent,RC_NICK_NAME)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_LOGIN->{
                info { resultCode }
                if(resultCode ==Activity.RESULT_OK) {
                        startActivityForResult(Intent(this,NickNameActivity::class.java),RC_NICK_NAME)
                }else{
                    val user =  FirebaseAuth.getInstance()
                    onAuthStateChanged(user)
                }
            }
            RC_NICK_NAME->{
                info { resultCode== Activity.RESULT_OK }
                info { data!!.getStringExtra(NICK_NAME) }
                if (resultCode== Activity.RESULT_OK){
                    val email = FirebaseAuth.getInstance().currentUser!!.email
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    val nickname =  data!!.getStringExtra(NICK_NAME)
                    val user = Users(uid,email!!,nickname)

                    firestore.collection("users").document(user.uid).set(user).addOnCompleteListener {
                        if(it.isSuccessful){
                            nickName = nickname
                            Toast.makeText(this,"Upload Susses",Toast.LENGTH_LONG).show()
                        }else{
                            AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage(it.exception?.message)
                                .setPositiveButton("OK",null)
                                .show()
                        }
                    }
                }
            }
            RC_SEARCHROOM->{
                if (resultCode== Activity.RESULT_OK){
                    startActivityForResult(
                        Intent(this,WaitingRoomActivity::class.java),
                        RC_WAITROOM
                    )
                }
            }
            RC_WAITROOM->{
                if (resultCode == Activity.RESULT_OK){
                    startActivityForResult(Intent(this,RoleActivity::class.java),RC_ROLE)
                }
            }

        }
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        var currentUser = Users()
        if (user!=null){
            firestore.collection("users").document(user.uid).get().addOnSuccessListener {
                currentUser = it.toObject(Users::class.java)!!
                nickName = currentUser.nickname
                Toast.makeText(this@MainActivity,"歡迎回來${currentUser.nickname}",Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this,"初次使用請進行登入或註冊",Toast.LENGTH_LONG).show()
        }
    }

    fun getRoomID () : String{
        val num =  Random.nextInt(999999).toString()
//    println(num)
        var id = ""
        if (num.length<6){
            for (i in 0 until  (6-num.length)){
                id+="0"
            }
        }
        id+=num
        return id
    }
}
