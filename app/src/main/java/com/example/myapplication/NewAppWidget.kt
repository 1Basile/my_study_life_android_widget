package com.example.myapplication

import DatabaseHandler
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Implementation of App Widget functionality.
 */

const val ACTION_ON_ITEM_CLICK = "ON_MORE_CLICK"
const val ACTION_ADD_NEW_TASK = "ADD_NEW_TASK"
const val ACTION_SYNC_TASK = "REFRESH_TASK"
const val ITEM_POSITION = "ITEM_POSITION"
lateinit var appWidgetIdsArray: IntArray;

class NewAppWidget : AppWidgetProvider() {

    //private var widgetServiceIntent: Intent? = null
    private lateinit var mService: WidgetService
    private var mBound: Boolean = false
    /** Defines callbacks for service binding, passed to bindService()  */
    //private val connection = object : ServiceConnection {
    //        // NOT USED IN PROJECT
    //        override fun onServiceConnected(className: ComponentName, service: IBinder) {
    //            // We've bound to LocalService, cast the IBinder and get LocalService instance
    //            val binder = service as WidgetService.LocalBinder
    //            mService = binder.getService()
    //            mBound = true
    //        }
    //
    //        override fun onServiceDisconnected(arg0: ComponentName) {
    //            mBound = false
    //        }
    //    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        appWidgetIdsArray = appWidgetIds
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.wiget_list_view)
    }

    override fun onEnabled(context: Context) {
        Log.e("Debug", "First widget created")

        sycDoSubjUpdate()
        sycDoTaskUpdate(context)
    }

    override fun onDisabled(context: Context) {
        Log.e("Debug", "Last widget deleted")

        SubjList = HashMap<String, MyStudyLifeSubjProperty>()
        val databaseHandler = DatabaseHandler(context)
        databaseHandler.clearAllTask()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            Log.e("OnRecive intent", intent.action.toString())
            when (intent.action) {
                ACTION_ON_ITEM_CLICK -> {
                    val clickedPosition = intent.getIntExtra(ITEM_POSITION, 0)
                    parseItemClick(context, clickedPosition)
                }
                ACTION_ADD_NEW_TASK -> {
                    addButtonClick(context)
                }
                ACTION_SYNC_TASK -> {
                    syncButtonClick(context)
                }
            }
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // config list view
    val listViewServiceIntent = Intent(context, ListViewItemService::class.java)
    listViewServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    listViewServiceIntent.data = Uri.parse(listViewServiceIntent.toUri(Intent.URI_INTENT_SCHEME))

    // add new task button config
    val onPlusClickIntent = Intent(context, NewAppWidget::class.java)
    onPlusClickIntent.action = ACTION_ADD_NEW_TASK
    val pendingOnPlusClickIntent = PendingIntent.getBroadcast(context, 0, onPlusClickIntent, 0)

    // add refresh button config
    val onRefreshClickIntent = Intent(context, NewAppWidget::class.java)
    onRefreshClickIntent.action = ACTION_SYNC_TASK
    val pendingOnRefreshClickIntent = PendingIntent.getBroadcast(context, 0, onRefreshClickIntent, 0)

    // config on click action template for list
    val onItemClick = Intent(context, NewAppWidget::class.java)
    onItemClick.action = ACTION_ON_ITEM_CLICK
    // onItemClick.data = Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME))
    val onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClick, 0)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setRemoteAdapter(R.id.wiget_list_view, listViewServiceIntent)
    views.setEmptyView(R.id.wiget_list_view, R.layout.linear_view_item)
    views.setPendingIntentTemplate(R.id.wiget_list_view, onClickPendingIntent)
    views.setOnClickPendingIntent(R.id.add_new_task_button, pendingOnPlusClickIntent)
    views.setOnClickPendingIntent(R.id.sync_task_button, pendingOnRefreshClickIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

@SuppressLint("SimpleDateFormat")
fun parseItemClick(context: Context, position:Int) {

    if (!isOnline(context)) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_SHORT).show()
        Log.e("Debug", "No connection to server.")
        return
    }

    // do some stuff
    val databaseHandler = DatabaseHandler(context)
    val currentMslItem = databaseHandler.viewMslTasks()[position]
    Log.e("Info", databaseHandler.viewMslTasks()[position].title)


     // change server item status
    if (currentMslItem.completed_at != null) {
        currentMslItem.progress = (0..100).random()
        currentMslItem.completed_at = null
        Log.e("Debug", "Item changed | Task undone")
    } else {
        currentMslItem.completed_at = SimpleDateFormat("yyyy-MM-dd;hh:mm:ss.SSS")
                .format(Date()).toString()
                .replace(";", "T")
        currentMslItem.progress = 100
        Log.e("Debug", "Item changed | Task done")
    }

    // send changes to server
    val success = syncSendTaskChangedToServer(currentMslItem)

    // if app is online, but main process in closed
    if (isOnline(context) && !success) {
        openApp(context, "My Study Life Widgets", "com.example.myapplication",
        mapOf("ifToHide" to "true"))
        syncSendTaskChangedToServer(currentMslItem)
    }

    // update widget and database
    sendWidgetTableUpdateNotification(context)

    //Toast.makeText(context, "App is running - " +
    //                "${(isOnline(context) && !success}" + "\n"
    //                +  databaseHandler.viewMslTasks()[position].title + " progress - " +
    //        databaseHandler.viewMslTasks()[position].progress.toString() +
    //        "\nData was sent to server - $success", Toast.LENGTH_LONG).show()
}

fun addButtonClick(context: Context) {
    openApp(context, "My Study Life", "com.virblue.mystudylife")
    Log.e("Debug", "Application is opened")
}

fun syncButtonClick(context: Context) {
    if (!isOnline(context)) {
        Toast.makeText(context, "No connection to server.", Toast.LENGTH_SHORT).show()
        Log.e("Debug", "No connection to server.")
        return
    }
    sendWidgetTableUpdateNotification(context)
    Log.e("Debug", "Sync is ok.")
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}

fun sendWidgetTableUpdateNotification(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val name = ComponentName(context, NewAppWidget::class.java)
    val ids = appWidgetManager.getAppWidgetIds(name)
    appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.wiget_list_view)
}