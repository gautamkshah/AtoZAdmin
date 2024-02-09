package com.example.atozadmin.api


import com.example.atozadmin.model.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {


    @Headers(
        "Content-Type: application/json",
        "Authorization: key=cpx76gJXSeihHaDSsEceBl:APA91bGXVUQO0dzuGioI640FRa5aldN_TPNeJ_7gq-KsGmW1JMt3UVzNmw0WbEjCKYtF3le3NONIBT8yxQDtQvFbIEXDNZCHmtLGIg5pkpQZMo0TVgJw4-DSbvB7sJew5bNHSEqhGder"

    )
    @POST("fcm/send")
    fun onSendNotification(@Body notification: Notification): Call<Notification >


}