package com.ignitedminds.blockit.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ignitedminds.blockit.R

class FilterAdapter(
    private val categories: List<String>,
    private val filterAdapterListener: FilterAdapterClickListener
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    lateinit var activeCategoryView: View

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val session: TextView = itemView.findViewById(R.id.categoryText)
        val root: View = itemView.findViewById(R.id.root)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterAdapter.ViewHolder, position: Int) {
        holder.session.text = categories[position]



        holder.root.setOnClickListener {
            changeActiveCategory(it!!)
            filterAdapterListener.onFilterAdapterClick(categories[position].lowercase())
        }

        if (position == 0) {
            activeCategoryView = holder.root
            changeActiveCategory(holder.root)
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    private fun changeActiveCategory(view: View) {
        setViewAsInactive(activeCategoryView)
        setViewAsActive(view)
        activeCategoryView = view
    }

    private fun setViewAsActive(root: View) {
        val categoryText = root.findViewById<TextView>(R.id.categoryText)
        root.setBackgroundResource(R.drawable.category_box_selected)
        categoryText.setTextColor(Color.WHITE)
    }

    private fun setViewAsInactive(root: View) {
        val categoryText = root.findViewById<TextView>(R.id.categoryText)
        root.setBackgroundResource(R.drawable.category_box)
        categoryText.setTextColor(Color.BLACK)
    }

    interface FilterAdapterClickListener {
        fun onFilterAdapterClick(filter : String)
    }
}