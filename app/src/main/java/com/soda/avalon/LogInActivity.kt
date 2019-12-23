package com.soda.avalon

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import org.jetbrains.anko.contentView

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        val auth = FirebaseAuth.getInstance()


        if (auth.currentUser!=null){
            AlertDialog.Builder(this)
                .setTitle("提醒")
                .setTitle("目前${auth.currentUser!!.email}已經登入\n是否進行登出")
                .setPositiveButton("登出") { dialog, which ->
                    Snackbar.make(contentView!!, "${auth.currentUser!!.email}登出成功", Snackbar.LENGTH_LONG).show();
                    auth.signOut()
                }
                .setNegativeButton("取消"){dialog, which ->
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                .setCancelable(false)
                .show()
        }

        bt_signup.setOnClickListener {
            signUp()
        }
        bt_login.setOnClickListener {
            Login()
        }

    }

    private fun Login() {
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "帳號或密碼不得為空", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "$email 歡迎回來", Toast.LENGTH_SHORT).show()
                        finish()

                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("登入失敗")
                            .setMessage(it.exception?.message)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
        }
    }

    private fun signUp() {
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "帳號或密碼不得為空", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        AlertDialog.Builder(this)
                            .setTitle("註冊成功")
                            .setTitle("下一步請輸入暱稱")
                            .setPositiveButton("OK") { dialog, which ->
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .show()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("註冊失敗")
                            .setMessage(it.exception?.message)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
        }
    }


}
