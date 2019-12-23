package com.soda.avalon

import android.R
import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.TextView



class ViewLoginDialog{
    fun showDialog(activity: Activity?) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.soda.avalon.R.layout.login_dialog)



        val dialogButton: Button = dialog.findViewById(com.soda.avalon.R.id.bt_dialogg_ok) as Button
        dialogButton.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
}