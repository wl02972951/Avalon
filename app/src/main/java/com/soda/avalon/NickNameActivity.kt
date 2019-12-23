package com.soda.avalon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_nick_name.*
val NICK_NAME = "NICKNAME"
class NickNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nick_name)
        val auth = FirebaseAuth.getInstance()
        val data = Intent()


        tv_email.text = "${auth.currentUser!!.email}你好\n請輸入您遊戲內想使用的暱稱"

        bt_ok.setOnClickListener {
            val nickname = et_nick.text.toString()
            if(nickname.isBlank()){
                Toast.makeText(this@NickNameActivity,"暱稱請勿空白",Toast.LENGTH_LONG).show()
            }else{
                data.putExtra(NICK_NAME,nickname)
                setResult(Activity.RESULT_OK,data)
                finish()
            }
        }

    }
}
