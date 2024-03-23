package com.ignitedminds.blockit.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogUtility {

    companion object{
        fun showDialog(context : Context, title : String, description : String){
            MaterialAlertDialogBuilder(context).setTitle(title).setMessage(description).setPositiveButton("OK"){ dialog, which->
                dialog.dismiss()
            }.show()
        }
    }
}