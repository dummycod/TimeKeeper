package com.ignitedminds.blockit.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ignitedminds.blockit.R

class DailySessionAdapter(
    private val sessions : List<String>
    ) : RecyclerView.Adapter<DailySessionAdapter.ViewHolder>(){

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val session : TextView = itemView.findViewById(R.id.session)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.active_session,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.session.text = sessions[position]
    }

    override fun getItemCount(): Int = sessions.size
}