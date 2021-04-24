package com.slazzer.bgremover.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiRequest {
    @Multipart
    @POST("v2.0/remove_image_background")
    fun upload(@Header("API-KEY") header: String, @Part file: MultipartBody.Part?): Call<ResponseBody?>?
}