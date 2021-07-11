package com.example.myapplication

import android.widget.EditText
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://api.mystudylife.com/v6.1/"

val server_api_headers = HashMap<String, String>()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

interface MyStudyLifeApiService {
    @GET("tasks")
    fun getTasks(@HeaderMap headers: Map<String, String>):
            Call<List<MyStudyLifeTasksProperty>>

    @GET("subjects")
    fun getSubjects(@HeaderMap headers: Map<String, String>):
            Call<List<MyStudyLifeSubjProperty>>

    @GET("exams")
    fun getExams(@HeaderMap headers: Map<String, String>):
            Call<List<MyStudyLifeExamsProperty>>

    @PUT("tasks/{guid}")
    fun changeTaskServerStatus(
            @HeaderMap headers: Map<String, String>,
            @Path("guid") guid: String,
            @Body mls: MyStudyLifeTasksProperty
    ): Call<BoolAnswer>
}
object MyStudyLifeApi {
    val retrofitService : MyStudyLifeApiService by lazy {
        retrofit.create(MyStudyLifeApiService::class.java)
    }
}
