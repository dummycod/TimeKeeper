package com.ignitedminds.blockit.ui.dialog


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.adapters.TodayAppUsageAdapter
import com.ignitedminds.blockit.databinding.SearchAppDialogBinding
import com.ignitedminds.blockit.utils.Utility


class SearchAppDialog(
    private val data: LinkedHashMap<String, String>,
    private val lockedApps: List<String>,
    val onAppInfoClick: (String) -> Unit
) : DialogFragment(), TodayAppUsageAdapter.TodayAppUsageInfoListener {

    private lateinit var binding: SearchAppDialogBinding
    private lateinit var todayAppUsageAdapter: TodayAppUsageAdapter
    private var todayData = data


    override fun onInfoClick(packageName: String) {
        clearSearchFocus()
        onAppInfoClick(packageName)
        dismiss()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_app_dialog, container, false)
        initializeView()
        return binding.root
    }


    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }


    private fun clearSearchFocus() {
        binding.search.clearFocus()
    }


    private fun initializeView() {
        initializeDataList()
        setClickListeners()
        putFocusOnSearch()
    }

    private fun initializeDataList() {
        binding.appUsage.layoutManager = LinearLayoutManager(context)
        todayAppUsageAdapter = TodayAppUsageAdapter(
            data,
            lockedApps,
            requireContext(),
            this
        )
        binding.appUsage.adapter = todayAppUsageAdapter
    }

    private fun setClickListeners() {
        binding.apply {
            close.setOnClickListener {
                dismiss()
            }
            onSearchEnterKeyHideKeyboard()
            onSearchChangeText()
        }
    }

    private fun putFocusOnSearch() {
        binding.search.requestFocus()
        showKeyboard(binding.search)
    }


    private fun onSearchEnterKeyHideKeyboard() {
        //On search box search press hide keyboard
        binding.apply {
            search.setOnKeyListener { view, keyCode, event ->
                hideKeyboard(view)
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // hideKeyboard(search)
                    search.isFocusable = false
                    search.isFocusableInTouchMode = true
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        dialog!!.window!!.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
    }


    private fun onSearchChangeText() {
        binding.search.addTextChangedListener { changedText ->
            if (changedText.toString().isBlank()) {
                putAllDataInList()
            } else {
                val filteredData = getFilteredData(changedText.toString())
                putFilteredDataInList(filteredData)
            }
        }
    }

    private fun putAllDataInList() {
        todayAppUsageAdapter.updateDataSet(todayData)
    }

    private fun getFilteredData(changedSuffix: String): HashMap<String, String> {
        return HashMap(todayData.filter {
            Utility.getApplicationName(it.key).lowercase()
                .startsWith(changedSuffix.lowercase())
        })
    }

    private fun putFilteredDataInList(filteredData: HashMap<String, String>) {
        binding.apply {
            if (filteredData.isEmpty()) {
                appUsage.visibility = View.GONE
                noData.visibility = View.VISIBLE
            } else {
                appUsage.visibility = View.VISIBLE
                noData.visibility = View.GONE
                todayAppUsageAdapter.updateDataSet(filteredData)
            }
        }
    }
}