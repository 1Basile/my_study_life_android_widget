package com.example.myapplication

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast


fun openApp(context: Context, appName: String, packageName: String, extras:Map<String, String> = emptyMap()) {
    if (isAppInstalled(context, packageName))
        if (isAppEnabled(context, packageName)) {
            val activity = context.packageManager.getLaunchIntentForPackage(packageName)
            for ((name, item: String) in extras.entries) {
                activity?.putExtra(name, item)
            }
            context.startActivity(activity)
        }
        else Toast.makeText(context, "$appName app is not enabled.", Toast.LENGTH_SHORT).show()
    else Toast.makeText(context, "$appName app is not installed.", Toast.LENGTH_SHORT).show()
}

 fun isAppInstalled(context: Context, packageName: String): Boolean {
    val pm: PackageManager = context.packageManager
    try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (ignored: PackageManager.NameNotFoundException) {
    }
    return false
}

 fun isAppEnabled(context: Context, packageName: String): Boolean {
    var appStatus = false
    try {
        val ai: ApplicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
        appStatus = ai.enabled
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return appStatus
}

fun isAppRunning(context: Context, packageName: String): Boolean {
    val am: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfo: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
    val tasks = am.getRunningTasks(1)
    if (tasks.isEmpty()) return false
    return tasks[0].topActivity!!.packageName != packageName
}