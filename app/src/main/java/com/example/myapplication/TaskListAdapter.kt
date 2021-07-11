package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class TaskListAdapter(private val context: Context,
                        private val dataSource: ArrayList<TableEnteryItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                                                                                   as LayoutInflater

    override fun getCount() = dataSource.size

    override fun getItem(position: Int) = dataSource[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.linear_view_item, parent, false)

        val roundImView = rowView.findViewById<ImageView>(R.id.round_gray_image)
        val subjectName = rowView.findViewById<TextView>(R.id.subject_name)
        val taskText = rowView.findViewById<TextView>(R.id.task_text)
        val timeLeftToDo = rowView.findViewById<TextView>(R.id.time_left_to_do)
        val percentOfDone = rowView.findViewById<TextView>(R.id.percent_of_done_text)
        val percentImView = rowView.findViewById<ImageView>(R.id.percentImView)

        val currentItem: TableEnteryItem = getItem(position)

        roundImView.setImageResource(currentItem.imageResource)
        percentImView.setImageResource(currentItem.ImViewNearPercent)
        subjectName.text = currentItem.Subject_name
        taskText.text = currentItem.Task_text
        timeLeftToDo.text = currentItem.Date_to_do
        timeLeftToDo.setTextColor(currentItem.Percent_of_done_text_color)
        percentOfDone.text = currentItem.Percent_of_done


        return rowView
    }

}