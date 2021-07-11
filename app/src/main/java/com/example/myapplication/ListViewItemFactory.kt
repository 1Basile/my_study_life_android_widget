package com.example.myapplication

import DatabaseHandler
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response
import java.lang.Math.abs
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// to give other modules access to
var SubjList = HashMap<String, MyStudyLifeSubjProperty>()
var NEED_SUBJ_REFRSH = true
var WIDGET_NUMBER = 0


class ListViewItemService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListViewItemFactory(this.applicationContext)
    }
}

class ListViewItemFactory(
        private val context: Context): RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {
        // connect to data source
        Log.e("Debug", "Create new widget instance")
        if (WIDGET_NUMBER == 0) {
            Log.e("Debug", "First widget runs, start service")
            //widgetServiceIntent = Intent(context, WidgetService::class.java)
            //context.startService(widgetServiceIntent)
        }
        WIDGET_NUMBER++
    }

    override fun onDestroy() {
        WIDGET_NUMBER -= 1
        if (WIDGET_NUMBER == 0) {
            Log.e("Debug", "Last widget destroyed, stop service")
            //context.stopService(widgetServiceIntent)
            val databaseHandler = DatabaseHandler(context)
            databaseHandler.clearAllTask()
        }
        Log.e("Debug", "Widget left $WIDGET_NUMBER")
        // clear data
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
        Log.e("Debug", "Runs onDataSetChanged")
        sycDoTaskUpdate(context)
        if (NEED_SUBJ_REFRSH) {
            sycDoSubjUpdate()
            sycDoTaskUpdate(context)
        }
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int): RemoteViews {

        val views = RemoteViews(context.packageName, R.layout.linear_view_item)
        val databaseHandler = DatabaseHandler(context)
        val item = databaseHandler.viewTasks()[position]

        views.setTextViewText(R.id.subject_name, item.Subject_name)
        views.setTextViewText(R.id.task_text, item.Task_text)
        views.setImageViewResource(R.id.round_gray_image, item.imageResource)
        views.setImageViewResource(R.id.percentImView, item.ImViewNearPercent)
        views.setTextViewText(R.id.time_left_to_do, item.Date_to_do)

        if (item.Percent_of_done == "100%") {
            //cross Type
            views.setInt(R.id.subject_name, "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)

            //cross Task Text
            views.setInt(R.id.task_text, "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)

            // cross and uncoloring due date
            views.setInt(R.id.time_left_to_do, "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
            views.setTextColor(R.id.time_left_to_do, DKGRAY)

            // set empty percent of done field
            views.setTextViewText(R.id.percent_of_done_text, "")

            views.setImageViewResource(R.id.percentImView, R.drawable.done_task_img)
        }
        else {
            views.setTextColor(R.id.time_left_to_do, item.Percent_of_done_text_color)
            views.setTextViewText(R.id.percent_of_done_text, item.Percent_of_done)
            // remove crossing
            views.setInt(R.id.subject_name, "setPaintFlags",
                    Paint.ANTI_ALIAS_FLAG)
            views.setInt(R.id.task_text, "setPaintFlags",
                    Paint.ANTI_ALIAS_FLAG)
            views.setInt(R.id.time_left_to_do, "setPaintFlags",
                    Paint.ANTI_ALIAS_FLAG)
            views.setImageViewResource(R.id.percentImView, R.drawable.ic_near_pcent_pen)

        }

        views.setOnClickFillInIntent(R.id.table_item_layout, createItemIntent(position))

        return views
    }

    override fun getCount() = DatabaseHandler(context).viewTasks().size

    override fun getViewTypeCount() = 1

}

fun createItemIntent(position: Int): Intent {
    val intent = Intent()
    intent.putExtra(ITEM_POSITION, position)
    return intent
}

private fun generateDummyList(size: Int): ArrayList<TableEnteryItem> {
    val list = ArrayList<TableEnteryItem>()

    for (i in 0 until size) {
        val drawable = when (i % 3) {
            0    -> R.drawable.ic_round_0_light_green
            1    -> R.drawable.ic_round_10_blue_gray
            else -> R.drawable.ic_round_13_dark_green
        }

        val subjectName = when (i % 3) {
            0    -> "Func. analysis"
            1    -> "Calculus"
            else -> "Operation. Research"
        }

        val percentageOfDone = when (i % 3) {
            0    -> ((46 + i*73) % 101).toString() + "%"
            1    -> ((46 + i*59) % 101).toString() + "%"
            else -> ((46 + i*39) % 101).toString() + "%"
        }

        val dateToDo = when (i % 3) {
            0    -> ((46 + i*73) % 31)
            1    -> ((46 + i*43) % 31)
            else -> ((46 + i*23) % 31)
        }

        val _time_left = dateToDo - 15   // now is 15-th
        val timeMarker:String
        val percentOfDoneTextColor: Any
        when {
            _time_left == 0 -> {
                timeMarker = "Due Today"
                percentOfDoneTextColor = Color.parseColor("#C8A951")
            }
            _time_left < 0  -> {
                timeMarker = "Overdue by ${abs(_time_left)} days";
                percentOfDoneTextColor = RED
            }
            _time_left < 7  -> {
                timeMarker = "$_time_left days to complete";
                percentOfDoneTextColor = Color.parseColor("#C8A951")
            }
            else            -> {
                timeMarker = "Due $dateToDo May";
                percentOfDoneTextColor = DKGRAY
            }
        }

        val percentImView = R.drawable.ic_near_pcent_pen

        val item = TableEnteryItem(imageResource=drawable, Subject_name=subjectName,
                Task_text="Do hw №$i", Percent_of_done=percentageOfDone,
                Date_to_do=timeMarker, ImViewNearPercent=percentImView,
                Percent_of_done_text_color=percentOfDoneTextColor,
                GUID="$i", UpdatedTime="", __dateForOrderBy=dateToDo.toString())
        list += item
    }
    return  list
}

fun sycDoSubjUpdate() {
    Log.e("Debug", "Doing subj update")
    try {
        val response: Response<List<MyStudyLifeSubjProperty>> =
            MyStudyLifeApi.retrofitService.getSubjects(server_api_headers).execute()
        response.body()?.let { mslSubjUpdate(it) }

        Log.e("Debug", "Subject updated.")
        //Toast.makeText(context, "Subject updated.", Toast.LENGTH_LONG).show()
    }
    catch (ex: Exception) {
        //Toast.makeText(context, "No connection to server.", Toast.LENGTH_LONG).show()
        Log.e("Debug", "No connection to server.")
    }

}

fun sycDoTaskUpdate(context: Context) {
    Log.e("Debug", "Start Task Update")
    try {
        Log.e("Debug_", "${server_api_headers["Authorization"]}")
        val response =
            MyStudyLifeApi.retrofitService.getTasks(server_api_headers).execute().body()!!
        // get current data
        val databaseHandler = DatabaseHandler(context)

        var TableItemsList = databaseHandler.viewTasks()
        var i=0
        // Do some data update

        // Check if some tasks deleted
        for (item in TableItemsList) {
            if (item.GUID !in response.map {it.guid}) {
                databaseHandler.clearAllTask()     // clear database
                TableItemsList = ArrayList<TableEnteryItem>()
                break
            }
        }

        for (item in response) {

            // do subj update if needed
            if (item.subject_guid !in SUBJ_GUID_TO_SUBJ_NAME.keys) {
                // need update subjects
                NEED_SUBJ_REFRSH = true
                Log.e("Debug", "Need task refresh $NEED_SUBJ_REFRSH")
                return
            }

            // if element changed
            if ( item.guid !in TableItemsList.map {it.GUID} ||
                item.updated_at != TableItemsList.find { it.GUID == item.guid }?.UpdatedTime) {

                // already done and not today
                if (item.completed_at != null) {
                    if (ChronoUnit.DAYS.between(LocalDate.now(),
                            LocalDate.parse(item.completed_at?.substringBefore("T", ""),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .toInt() != 0) {
                        continue
                    }
                }

                val tableItem = myStudyLifePropertyToTableEnteryConvert(item)
                if (tableItem == null) {
                    NEED_SUBJ_REFRSH = true
                    return
                }

                // update or add new element to database
                if (item.guid in TableItemsList.map {it.GUID}) {
                    TableItemsList = ArrayList(TableItemsList.filterNot { it.GUID == item.guid })
                    databaseHandler.updateTask(tableItem)
                    databaseHandler.deleteMslTask(item)
                    //databaseHandler.deleteTask(tableItem)
                    //databaseHandler.addTask(tableItem)
                    databaseHandler.addMslTask(item)
                } else {
                    TableItemsList.toMutableList().add(tableItem)
                    databaseHandler.addTask(tableItem)
                    databaseHandler.addMslTask(item)
                }
                i+=1
            }
        }

        Log.e("Debug", "$i tasks updated.")
    }
    catch (ex: Exception) {
        Log.e("Debug", ex.toString())
    }

}

fun syncSendTaskChangedToServer(item: MyStudyLifeTasksProperty): Boolean {
    Log.e("Debug", "Start task status changing")
    try {
        val response: Response<BoolAnswer> =
                MyStudyLifeApi.retrofitService.changeTaskServerStatus(server_api_headers, item.guid, item).execute()

        val _response = "Change status of ${item.title} - ${response.message()}"
        Log.e("Debug", _response)
        return response.isSuccessful
    }
    catch (ex: Exception) {
        Log.e("Debug", "No connection to server.")
        return false
    }
}

fun asynDoSubjUpdate(context: Context) {
    MyStudyLifeApi.retrofitService.getSubjects(server_api_headers).enqueue(__DoSubjUpdate(context))
}

fun asynDoTaskUpdate(context: Context) {
    MyStudyLifeApi.retrofitService.getTasks(server_api_headers).enqueue(__DoTaskUpdate(context))
}

fun asynSendTaskChangedToServer(context: Context, item: MyStudyLifeTasksProperty) {
    MyStudyLifeApi.retrofitService.changeTaskServerStatus(server_api_headers, item.guid, item).enqueue(__ChangeTaskStatus(context))
}

fun myStudyLifePropertyToTableEnteryConvert(msl: MyStudyLifeTasksProperty): TableEnteryItem? {

    // if subj database has not updated
    if (msl.subject_guid !in SUBJ_GUID_TO_SUBJ_NAME.keys) {
        // need update subjects
        NEED_SUBJ_REFRSH = true
        return null
    }
    val toDoDate = LocalDate.parse(msl.due_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val datediff = ChronoUnit.DAYS.between(LocalDate.now(), toDoDate).toInt()
    var timeMarker: String
    var percentOfDoneTextColor: Int

    // customize
    when {
        datediff == 0 -> {
            timeMarker = "Due Today"
            percentOfDoneTextColor = Color.parseColor("#C8A951")
        }
        datediff < 0  -> {
            timeMarker = "Overdue by ${abs(datediff)} days";
            percentOfDoneTextColor = RED
        }
        datediff < 7  -> {
            timeMarker = "$datediff days to complete";
            percentOfDoneTextColor = Color.parseColor("#C8A951")
        }
        else            -> {
            timeMarker = "Due ${toDoDate.dayOfMonth} ${toDoDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}";
            percentOfDoneTextColor = DKGRAY
        }
    }

    val item = TableEnteryItem(
            imageResource = SUBJ_GUID_TO_COLOR_DICT[msl.subject_guid]!!,
            Subject_name = "${SUBJ_GUID_TO_SUBJ_NAME[msl.subject_guid]!!} ${msl.type.capitalize()}",
            Task_text = msl.title,
            Date_to_do = timeMarker,
            Percent_of_done = "${msl.progress}%",
            Percent_of_done_text_color = percentOfDoneTextColor,
            GUID = msl.guid,
            UpdatedTime = "${msl.updated_at}",
            __dateForOrderBy = msl.due_date)

    return item
}

fun mslSubjUpdate(new_tasks_list: List<MyStudyLifeSubjProperty>) {
    for (subject in new_tasks_list) {
        if (!SubjList.containsKey(subject.guid) || SubjList[subject.guid]!!.updated_at != subject.updated_at) {
            COLOR_NUM_TO_COLOR_ADDRESS[subject.color.toInt()]?.let { SUBJ_GUID_TO_COLOR_DICT.put(subject.guid, it) }
            SUBJ_GUID_TO_SUBJ_NAME[subject.guid] = subject.name
            SubjList[subject.name] = subject
        }
    }
    NEED_SUBJ_REFRSH = false
}

private class __DoTaskUpdate(val context: Context) : retrofit2.Callback<List<MyStudyLifeTasksProperty>> {
    override fun onFailure(call: Call<List<MyStudyLifeTasksProperty>>, t: Throwable) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
        call: Call<List<MyStudyLifeTasksProperty>>,
        response: Response<List<MyStudyLifeTasksProperty>>
    ) {
        var i = 0

        // get current data
        val databaseHandler = DatabaseHandler(context)
        var TableItemsList = databaseHandler.viewTasks()

        // Do some data update
        for (item in response.body()!!) {

            // do subj update if needed
            if (item.subject_guid !in SUBJ_GUID_TO_SUBJ_NAME.keys) {
                // need update subjects
                NEED_SUBJ_REFRSH = true
                Log.e("Debug", "Need task refresh $NEED_SUBJ_REFRSH")
                return
            }

            // if element changed
            if ( item.guid !in TableItemsList.map {it.GUID} ||
                item.updated_at != TableItemsList.find { it.GUID == item.guid }?.UpdatedTime) {

                // already done and not today
                if (item.completed_at != null) {
                    if (ChronoUnit.DAYS.between(LocalDate.now(),
                            LocalDate.parse(item.completed_at?.substringBefore("T", ""),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .toInt() != 0) {
                        continue
                    }
                }

                val tableItem = myStudyLifePropertyToTableEnteryConvert(item)
                if (tableItem == null) {
                    NEED_SUBJ_REFRSH = true
                    return
                }

                // update or add new element to database
                if (item.guid in TableItemsList.map {it.GUID}) {
                    TableItemsList = ArrayList(TableItemsList.filterNot { it.GUID == item.guid })
                    databaseHandler.updateTask(tableItem)
                } else {
                    TableItemsList.add(tableItem)
                    databaseHandler.addTask(tableItem)
                }
                i+=1
            }
        }

        val _response = "$i tasks updated."
        Log.e("Debug", _response)

        Toast.makeText(context, _response, Toast.LENGTH_LONG).show()
    }

}

private class __DoExamsUpdate(val context: Context) : retrofit2.Callback<List<MyStudyLifeExamsProperty>> {
    override fun onFailure(call: Call<List<MyStudyLifeExamsProperty>>, t: Throwable) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
        call: Call<List<MyStudyLifeExamsProperty>>,
        response: Response<List<MyStudyLifeExamsProperty>>
    ) {
        // Do some data update
        val _response = "Success: ${response.body()?.get(0)?.timestamp}"
        Log.e("Test", _response)

        Toast.makeText(context, _response, Toast.LENGTH_LONG).show()
    }

}

private class __DoSubjUpdate(val context: Context) : retrofit2.Callback<List<MyStudyLifeSubjProperty>> {
    override fun onFailure(call: Call<List<MyStudyLifeSubjProperty>>, t: Throwable) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
        call: Call<List<MyStudyLifeSubjProperty>>,
        response: Response<List<MyStudyLifeSubjProperty>>
    ) {

        // Do some data update
        response.body()?.let { mslSubjUpdate(it) }

        val _response = "Subject updated."
        Log.e("Debug", _response)

        Toast.makeText(context, _response, Toast.LENGTH_LONG).show()
    }

}

private class __ChangeTaskStatus(val context: Context) : retrofit2.Callback<BoolAnswer> {
    override fun onFailure(call: Call<BoolAnswer>, t: Throwable) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
            call: Call<BoolAnswer>,
            response: Response<BoolAnswer>
    ) {
        // Do some data update
        val _response = "Change status success - ${response.body()?.success}"
        Log.e("Debug", _response)

        Toast.makeText(context, _response, Toast.LENGTH_LONG).show()
    }

}
