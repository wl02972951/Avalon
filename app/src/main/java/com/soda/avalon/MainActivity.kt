package com.soda.avalon

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.email
import org.jetbrains.anko.info


class MainActivity : AppCompatActivity() , FirebaseAuth.AuthStateListener ,AnkoLogger{

    private val RC_NICK_NAME = 201
    private val RC_LOGIN = 199
    private val RC_GAME = 200
    private var IS_LOGIN = false
    val firestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val user =  FirebaseAuth.getInstance()

        onAuthStateChanged(user)

        bt_newgame.setOnClickListener {


        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "create your own function", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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

        }
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        var currentUser = Users()
        if (user!=null){
            IS_LOGIN = true
            firestore.collection("users").document(user.uid).get().addOnSuccessListener {
                currentUser = it.toObject(Users::class.java)!!
                Toast.makeText(this@MainActivity,"歡迎回來${currentUser.nickname}",Toast.LENGTH_SHORT).show()
            }

        }else{
            IS_LOGIN = false
            Toast.makeText(this,"初次使用請進行登入或註冊",Toast.LENGTH_LONG).show()
        }
    }
}
