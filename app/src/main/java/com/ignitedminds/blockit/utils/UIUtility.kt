package com.ignitedminds.blockit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.palette.graphics.Palette
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App.Companion.appContext
import com.ignitedminds.blockit.constants.Constant


class UIUtility {

    companion object BottomSheetUtility {


        lateinit var behavior: BottomSheetBehavior<View>

        fun setUpHeight(activity: FragmentActivity, bottomSheetDialog: BottomSheetDialog) {
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            behavior = BottomSheetBehavior.from(bottomSheet as View)

            behavior.addBottomSheetCallback(BottomSheetCallback)

            val layoutParams = bottomSheet.layoutParams

            val windowHeight = getWindowHeight(activity)
            layoutParams?.height = windowHeight
            bottomSheet.layoutParams = layoutParams
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        private fun getWindowHeight(activity: FragmentActivity): Int {

            val outMetrics = DisplayMetrics()

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val display = activity.display
                display?.getMetrics(outMetrics)
            } else {
                activity.windowManager?.defaultDisplay?.getMetrics((outMetrics))
            }
            return outMetrics.heightPixels
        }

        private object BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED;
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }


        }

        fun getColor(colorInt : Int) : Int{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appContext.resources.getColor(colorInt, appContext.theme)
            }else {
                appContext.resources.getColor(colorInt)
            }
        }


        fun getDominantColorFromImage(drawable: Drawable): Int {
            var color = 0
            val bitmap = drawable.toBitmap()
            val palette = Palette.Builder(bitmap).generate()
            color = palette.getDominantColor(0)
            return color
        }
    }

}