package com.ignitedminds.blockit.ui.dialog.todoDialogs


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.listeners.DialogActionListener
import com.ignitedminds.blockit.utils.UIUtility.BottomSheetUtility.setUpHeight


class TodoParentDialog(private val  TYPE : Int) : BottomSheetDialogFragment() {

    companion object{
        const val SHOW_LIST = 1
        const val ADD_NEW_TODO = 2
        const val OPEN_URL = 3
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_container,container,false)
        addFragment()
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            activity?.let { fragActivity -> setUpHeight(fragActivity,bottomSheetDialog) }
        }
        return dialog
    }

    private fun addFragment(){
        if(TYPE== SHOW_LIST){
            addShowTodoListPage()
        }else{
            addInsertTodoListPage()
        }
    }

    private fun addShowTodoListPage(){
        val todolist = TodoList(getTodoListListener(),::addWebViewPage)
        addPageToBottomSheet(todolist)
    }

    private fun addInsertTodoListPage(){
        val addItem = AddTodo(getAddTodoListener())
        addPageToBottomSheet(addItem)
    }

    private fun addWebViewPage(url : String){
        val todoWebView = TodoWebview(url)
        addPageToBottomSheet(todoWebView)
    }

    private fun addPageToBottomSheet(fragment : Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getTodoListListener() :DialogActionListener {
      return object  : DialogActionListener{
          override fun onAction() {
              addInsertTodoListPage()
          }

          override fun onCancel() {
                dismiss()
            }
        }
    }

    private fun getAddTodoListener() : DialogActionListener{
        return object : DialogActionListener{
            override fun onAction() {
            }

            override fun onCancel() {
                addShowTodoListPage()
            }

        }
    }


}