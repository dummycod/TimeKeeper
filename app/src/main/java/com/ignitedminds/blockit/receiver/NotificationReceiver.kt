package com.ignitedminds.blockit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.ui.dialog.todoDialogs.TodoParentDialog

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //val text = intent.getStringExtra("Link")?:"Null Text"
        //Toast.makeText(context,text,Toast.LENGTH_LONG).show()

        //Create A Dialog Box For Adding Content..

        Constant.logger("Clicked@NotificationReceiver")
    }
}