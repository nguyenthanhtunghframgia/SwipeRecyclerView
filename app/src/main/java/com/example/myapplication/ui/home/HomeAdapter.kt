package com.example.myapplication.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Project

class HomeAdapter(
    private var listProjects: MutableList<Project>? = null,
    private var context: Context? = null
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listProjects?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binData(listProjects?.get(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startDate: TextView = itemView.findViewById(R.id.tv_start_date)
        private val dueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        fun binData(project: Project?) {
            startDate.text = project?.startDate
            dueDate.text = project?.dueDate
        }
    }
}
