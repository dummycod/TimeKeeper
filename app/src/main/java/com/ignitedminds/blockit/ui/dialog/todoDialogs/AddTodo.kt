package com.ignitedminds.blockit.ui.dialog.todoDialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.local.sql.entities.LinkData
import com.ignitedminds.blockit.databinding.FragmentAddTodoBinding
import com.ignitedminds.blockit.listeners.DialogActionListener
import com.ignitedminds.blockit.utils.DialogUtility
import com.ignitedminds.blockit.utils.Utility
import com.ignitedminds.blockit.viewmodels.LinkDataViewModel
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import java.net.SocketTimeoutException


class AddTodo(private val dialogActionListener: DialogActionListener) : Fragment(),
    CoroutineScope by MainScope() {

    private lateinit var viewModel: LinkDataViewModel
    private lateinit var binding: FragmentAddTodoBinding

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private var link = ""
    private var imageUrl = ""
    private var linkTitle = ""
    private var linkDescription = ""

    val AGENT = "facebookexternalhit/1.1"
    val TIMEOUT = 10_000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_todo, container, false)
        viewModel = getViewModel()
        val view = binding.root
        initializeView()
        return view
    }

    private fun getViewModel(): LinkDataViewModel {
        return ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(App.application)
        )[LinkDataViewModel::class.java]
    }

    private suspend fun fetchData(url: String) =
        withContext(ioScope.coroutineContext) {
            try {
                if (!Utility.isNetworkConnected(requireContext())) {
                    throw Exception("No Internet Connection")
                }
                val doc =
                    Jsoup.connect(url).ignoreContentType(true).userAgent(AGENT).timeout(TIMEOUT)
                        .followRedirects(true)
                        .get()
                link = url
                linkTitle = doc.title()
                linkDescription = doc.select("meta[name=description]").attr("content")
                imageUrl = doc.select("meta[property=og:image]").attr("content")

                val linkData = LinkData(link, "none", imageUrl, linkTitle, linkDescription, 0)
                Constant.logger(linkData.toString())

            } catch (e: Exception) {
                throw  e
            }
        }

    private fun initializeView() {
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        setOnBackPressedListener()
        setTextChangeListenerToLinkSearchBox()
        setTextChangeListenerToTitleBox()
        setTextChangeListenerToDescriptionBox()
        setOnClearClickedListener()
        setOnFetchClickedListener()
        setOnSaveClickedListener()
    }

    private fun setOnSaveClickedListener() {
        binding.save.setOnClickListener {
            try {
                val linkData = getLinkData()
                viewModel.insertLink(linkData, ::showSuccessMessage)

            } catch (e: Exception) {
                DialogUtility.showDialog(requireContext(), "Error", e.message!!)
            }
        }
    }

    private fun getLinkData(): LinkData {
        if (link.isEmpty()) {
            throw Exception("Can't save data with a valid url")
        }

        if (linkTitle.isEmpty()) {
            throw  Exception("Title cannot be empty!")
        }

        if (linkDescription.isEmpty()) {
            throw Exception("Description cannot be empty")
        }

        val selectedType = getSelectedType()

        if (selectedType == "INVALID_TYPE") {
            throw Exception("Type Can Only Be Article or Video")
        }

        return LinkData(link, selectedType, imageUrl, linkTitle, linkDescription, 0)
    }

    private fun getSelectedType(): String {
        return when (binding.typeRadioGroup.checkedRadioButtonId) {
            R.id.radio_article -> {
                "ARTICLES"
            }
            R.id.radio_video -> {
                "VIDEOS"
            }
            else -> {
                "INVALID_TYPE"
            }
        }
    }

    private fun setOnFetchClickedListener() {
        binding.searchLink.setOnClickListener {
            launch {
                showLoadingAndHidePreview()
                try {
                    fetchData(getUrl())
                    hideLoadingAndShowPreview()
                } catch (exception: Exception) {
                    Constant.logger(exception.message!!)
                    hideLoadingIcon()
                    val message = getErrorMessage(exception)
                    DialogUtility.showDialog(requireContext(), "Error", message)
                }
            }
        }
    }

    private fun getUrl(): String {
        var url = binding.linkText.text.toString()
        if (!url.contains("https://")) {
            url = "https://$url"
        }
        return url
    }

    private fun getErrorMessage(e: Exception): String {
        return when (e) {
            is SocketTimeoutException -> {
                "Request Timeout"
            }
            is IOException -> {
                "Please check the URL entered"
            }
            is IllegalArgumentException -> {
                "BAD URL"
            }
            else -> {
                if (e.message == "No Internet Connection")
                    "No Internet Connection"
                else
                    "Something Went Wrong"
            }
        }
    }

    private fun setOnBackPressedListener() {
        binding.back.setOnClickListener {
            dialogActionListener.onCancel()
        }
    }

    private fun showLoadingAndHidePreview() {
        disableSearchButton()
        hideRichPreview()
        showLoadingIcon()
    }

    private fun hideLoadingAndShowPreview() {
        enableSearchButton()
        hideLoadingIcon()
        showRichPreview()
    }

    private fun enableSearchButton() {
        binding.searchLink.apply {
            isEnabled = true
            setBackgroundColor(resources.getColor(R.color.fetch_link_btn))
        }
    }

    private fun disableSearchButton() {
        binding.searchLink.apply {
            isEnabled = false
            setBackgroundColor(Color.LTGRAY)
        }
    }

    private fun setOnClearClickedListener() {
        binding.clearSearchText.setOnClickListener {
            binding.linkText.setText("")
        }
    }

    private fun enableClearButton() {
        binding.clearSearchText.apply {
            isEnabled = true
            setBackgroundColor(resources.getColor(R.color.material_red))
        }
    }

    private fun disableClearButton() {
        binding.clearSearchText.apply {
            isEnabled = false
            setBackgroundColor(Color.LTGRAY)
        }
    }

    private fun hideRichPreview() {
        binding.richPreview.visibility = View.GONE
    }

    private fun showRichPreview() {
        Glide.with(requireActivity()).load(imageUrl).error(R.drawable.error_image)
            .into(binding.linkImage)
        binding.titleEdt.setText(linkTitle)
        binding.descriptionEdt.setText(linkDescription)
        binding.richPreview.visibility = View.VISIBLE
    }

    private fun showLoadingIcon() {
        binding.loadingContainer.visibility = View.VISIBLE
    }

    private fun hideLoadingIcon() {
        binding.loadingContainer.visibility = View.GONE
    }


    private fun showSuccessMessage(message: String) {
        DialogUtility.showDialog(requireContext(), "Success", message)
    }

    private fun setTextChangeListenerToLinkSearchBox() {
        binding.linkText.addTextChangedListener {
            if (it!!.isEmpty()) {
                disableClearButton()
                disableSearchButton()
            } else {
                enableClearButton()
                enableSearchButton()
            }
        }
        binding.linkText.setText("")
    }

    private fun setTextChangeListenerToTitleBox() {
        binding.titleEdt.addTextChangedListener {
            linkTitle = it.toString()
        }
    }

    private fun setTextChangeListenerToDescriptionBox() {
        binding.descriptionEdt.addTextChangedListener {
            linkDescription = it.toString()
        }
    }


}