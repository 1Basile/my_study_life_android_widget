package com.example.myapplication

import com.squareup.moshi.Json

data class MyStudyLifeTasksProperty(
        @Json(name="completed_at")
    var completed_at: String?,
        @Json(name="created_at")
    val created_at: String,
        @Json(name="deleted_at")
    val deleted_at: String?,
        @Json(name="detail")
    val detail: String?,
        @Json(name="due_date")
    val due_date: String?,
        @Json(name="exam_guid")
    val exam_guid: String?,
        @Json(name="guid")
    var guid: String,
        @Json(name="progress")
    var progress: Int,
        @Json(name="school_id")
    val school_id: String?,
        @Json(name="subject_guid")
    val subject_guid: String,
        @Json(name="timestamp")
    val timestamp: Float,
        @Json(name="title")
    val title: String,
        @Json(name="type")
    val type: String,
        @Json(name="updated_at")
    val updated_at: String?,
        @Json(name="user_id")
    val user_id: Int
)

data class MyStudyLifeSubjProperty(
    val color: String,
    val created_at: String,
    val deleted_at: String?,
    val guid: String,
    val name: String,
    val school_id: String?,
    val term_guid: String?,
    val timestamp: Float,
    val updated_at: String?,
    val user_id: Int,
    val year_guid: String?
    )

data class MyStudyLifeExamsProperty(
    val created_at: String,
    val date: String?,
    val deleted_at: String?,
    val duration: Int,
    val guid: String,
    val module: String,
    val resit: Boolean,
    val room: String?,
    val school_id: String?,
    val seat: Int?,
    val subject_guid: String,
    val timestamp: Float,
    val updated_at: String?,
    val user_id: Int
)

data class BoolAnswer(
        val success: Boolean
)