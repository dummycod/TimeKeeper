package com.ignitedminds.blockit.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.listeners.LockedStatusListener

class AppInfoDialog(
    private val usageData: HashMap<String, Long>,
    private val packageName: String,
    private val sessionData: List<String>,
    private val lockedStatusListener: LockedStatusListener
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_container, container, false)
        addFirstPageToBottomSheet()
        return view
    }


    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog!!.window!!.setLayout(width, height)
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    private fun addFirstPageToBottomSheet() {
        val appInfoFragment =
            AppInfoFragment(usageData, packageName, sessionData, lockedStatusListener)
        addPageToBottomSheet(appInfoFragment)
    }

    private fun addPageToBottomSheet(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}