package com.ignitedminds.blockit.ui.dialog.todoDialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.ArrayList
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.adapters.FilterAdapter
import com.ignitedminds.blockit.adapters.TodoListAdapter
import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.data.local.sql.entities.LinkData
import com.ignitedminds.blockit.listeners.DialogActionListener
import com.ignitedminds.blockit.viewmodels.LinkDataViewModel


class TodoList(private val dialogActionListener: DialogActionListener, private val onUrlEntered : (url : String)->Unit) :
    Fragment(), FilterAdapter.FilterAdapterClickListener, TodoListAdapter.TodoListAdapterActions {

    private lateinit var viewModel: LinkDataViewModel
    lateinit var categoryList: RecyclerView
    lateinit var todoList: RecyclerView
    lateinit var todoListAdapter: TodoListAdapter
    private var activeFilter = "all"

    private var linkDataList = ArrayList<LinkData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todolist, container, false)
        viewModel = getViewModel()
        initializeView(view)
        return view
    }

    private fun getViewModel(): LinkDataViewModel {
        return ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(App.application)
        )[LinkDataViewModel::class.java]
    }

    private fun initializeView(view: View) {

        setOnClickListeners(view)

        categoryList = view.findViewById(R.id.category_list)
        initializeCategoryList()

        todoList = view.findViewById(R.id.todo_list)
        initializeTodoList()

        addObservers()
    }

    private fun setOnClickListeners(view: View) {
        val close = view.findViewById<ImageView>(R.id.close)
        val addItem = view.findViewById<TextView>(R.id.add_item)
        addItem.setOnClickListener {
            dialogActionListener.onAction()
        }

        close.setOnClickListener {
            dialogActionListener.onCancel()
        }
    }

    private fun initializeCategoryList() {
        categoryList.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryList.adapter = FilterAdapter(listOf("All", "Articles", "Videos", "Starred"),this)
    }

    private fun initializeTodoList() {
        todoList.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        setTodoListOnTouchListener()

        todoListAdapter = TodoListAdapter(this)

        todoList.adapter = todoListAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTodoListOnTouchListener() {
        todoList.setOnTouchListener { p0, p1 ->
            p0?.parent?.requestDisallowInterceptTouchEvent(true)
            p0?.onTouchEvent(p1)
            true
        }
    }

    private fun addObservers() {
        viewModel.allLinks.observe(this, Observer {
            it?.let {
                linkDataList.clear()
                linkDataList.addAll(it)
                val filteredList = getFilteredList()
                todoListAdapter.updateList(filteredList)
            }
        })
    }

    private fun getFilteredList(): List<LinkData> {
        return when (activeFilter) {

            "starred" -> linkDataList.filter { it.starred == 1 }

            "articles" -> linkDataList.filter { it.type == "ARTICLES" }

            "videos" -> linkDataList.filter { it.type == "VIDEOS" }

            else -> linkDataList
        }
    }

    override fun onFilterAdapterClick(filter: String) {
        activeFilter = filter
        val filteredList = getFilteredList()
        todoListAdapter.updateList(filteredList)
    }

    override fun onDeleteClicked(linkData: LinkData) {
        viewModel.deleteLink(linkData)
    }

    override fun onStarredClicked(linkData: LinkData) {
        if(linkData.starred==0)
            viewModel.addLinkToFavorites(linkData._id)
        else
            viewModel.removeLinkFromFavorites(linkData._id)
    }

    override fun onItemClicked(url : String) {
        onUrlEntered(url)
    }
}

