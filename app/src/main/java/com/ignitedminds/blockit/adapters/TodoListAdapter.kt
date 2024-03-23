package com.ignitedminds.blockit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.data.local.sql.entities.LinkData

class TodoListAdapter(private val todoListAdapterActions: TodoListAdapterActions) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    private val linksData = ArrayList<LinkData>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val root: View = itemView.findViewById(R.id.root)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val image: ImageView = itemView.findViewById(R.id.image)
        val starImage: ImageView = itemView.findViewById(R.id.star)
        val deleteImage: ImageView = itemView.findViewById(R.id.delete)
        val openLinkImage: ImageView = itemView.findViewById(R.id.open_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rich_link_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val linkData = linksData[position]
        holder.apply {
            title.text = linkData.title
            description.text = linkData.description
            Glide.with(App.appContext).load(linkData.imageUrl).error(R.drawable.error_image)
                .into(image)
            setTagsToStarImage(starImage, linkData.starred)
            setOnAddFavouriteClickedAndTags(starImage, linkData)
            setOnDeleteClickedListener(deleteImage, linkData)
            setOnOpenLinkClickedListener(openLinkImage, linkData.url)
        }
    }

    private fun setOnAddFavouriteClickedAndTags(starImage: ImageView, linkData: LinkData) {
        starImage.setOnClickListener {

            todoListAdapterActions.onStarredClicked(linkData)

            if (starImage.tag == R.drawable.star_fill) {
                starImage.setImageResource(R.drawable.star)
                starImage.tag = R.drawable.star
            } else {
                starImage.setImageResource(R.drawable.star_fill)
                starImage.tag = R.drawable.star_fill
            }
        }
    }

    private fun setTagsToStarImage(starImage: ImageView, isStarred: Int) {
        if (isStarred == 0) {
            starImage.tag = R.drawable.star
            starImage.setImageResource(R.drawable.star)
        } else {
            starImage.tag = R.drawable.star_fill
            starImage.setImageResource(R.drawable.star_fill)
        }
    }

    private fun setOnDeleteClickedListener(deleteImage: ImageView, linkData: LinkData) {
        deleteImage.setOnClickListener {
            todoListAdapterActions.onDeleteClicked(linkData)
        }
    }

    private fun setOnOpenLinkClickedListener(openLinkImage: ImageView, url: String) {
        openLinkImage.setOnClickListener {
            todoListAdapterActions.onItemClicked(url)
        }
    }

    private fun setOnRootClickedListener(root: View, url: String) {

        root.setOnClickListener {
            todoListAdapterActions.onItemClicked(url)
        }
    }

    fun updateList(mLinksData: List<LinkData>) {
        linksData.clear()
        linksData.addAll(mLinksData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return linksData.size
    }

    interface TodoListAdapterActions {
        fun onDeleteClicked(linkData: LinkData)
        fun onStarredClicked(linkData: LinkData)
        fun onItemClicked(url: String)
    }
}