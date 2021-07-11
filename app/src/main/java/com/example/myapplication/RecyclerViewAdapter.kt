package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val exampleList: List<TableEnteryItem>) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roundImView: ImageView = itemView.findViewById(R.id.round_gray_image)
        val subjectName: TextView = itemView.findViewById(R.id.subject_name)
        val taskText: TextView = itemView.findViewById(R.id.task_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.linear_view_item,
            parent, false
        )

        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.roundImView.setImageResource(currentItem.imageResource)
        holder.subjectName.text = currentItem.Subject_name
        holder.taskText.text = currentItem.Task_text
    }

    override fun getItemCount() = exampleList.size
}