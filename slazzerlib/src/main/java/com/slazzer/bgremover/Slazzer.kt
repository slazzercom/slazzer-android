package com.slazzer.bgremover

import com.slazzer.bgremover.network.ApiRequest
import com.slazzer.bgremover.network.ProgressRequestBody
import com.slazzer.bgremover.network.RetrofitRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object Slazzer {
    private var apiKey: String? = null
    /**
     * To initialize the API-KEY. Should be called before calling from method.
     */
    fun init(apiKey: String) {
        Slazzer.apiKey = apiKey
    }

    /**
     * To remove image background from the given file.
     */
    fun from(file: File, callback: ResponseCallback) {
        require(apiKey != null) { "You must call Slazzer.init before calling Slazzer.from" }
        if(apiKey!!.contains("YOUR-API-KEY")){
            callback.onError("Please enter your api key")
            return
        }
        callback.onProgressStart()

        val filePart = ProgressRequestBody(
            file,
            "image/png",
            object : ProgressRequestBody.ProgressListener {
                override fun fileTransferred(percentage: Float) {
                    callback.onProgressUpdate(percentage)
                }

            })

        // val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("source_image_file", file.name, filePart)
        val apiRequest: ApiRequest = RetrofitRequest.retrofitInstance!!.create(ApiRequest::class.java)

        apiRequest.upload(apiKey!!,body)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                callback.onProgressEnd()
                when {
                    response.body() != null -> {
                        callback.onSuccess(response.body()!!.string())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                callback.onProgressEnd()
                callback.onError(t.toString())
            }
        })

    }

    interface ResponseCallback {
        fun onProgressStart()
        fun onProgressEnd()
        fun onProgressUpdate(percentage: Float)
        fun onSuccess(response: String)
        fun onError(errors: String)
    }
}
