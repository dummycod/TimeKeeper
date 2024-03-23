package com.ignitedminds.blockit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.ui.dialog.todoDialogs.TodoParentDialog

class LockScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        val lockButton = findViewById<MaterialButton>(R.id.goto_todo)

        val meme_monkey = findViewById<LottieAnimationView>(R.id.lock_image)
        meme_monkey.playAnimation()

        lockButton.setOnClickListener {
            val todoParentDialogBottomSheet = TodoParentDialog(TodoParentDialog.SHOW_LIST)
            todoParentDialogBottomSheet.show(supportFragmentManager, "Create Profile Dialog..")
        }
    }
}