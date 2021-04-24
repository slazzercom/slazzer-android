package com.slazzer.bgremover

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.slazzer.bgremover.network.ApiRequest
import com.slazzer.bgremover.network.ProgressRequestBody
import com.slazzer.bgremover.network.RetrofitRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object Slazzer {
    private var apiKey: String? = null
    /**
     * To initialize the slazzer API-KEY. Should be called before calling get method.
     */
    fun init(apiKey: String) {
        Slazzer.apiKey = apiKey
    }

    /**
     * To remove image background from the given file.
     */
    fun get(sourceFile: File, callback: ResponseCallback) {
        require(apiKey != null) { "You must call Slazzer.init before calling Slazzer.get" }
        if(apiKey!!.contains("YOUR-API-KEY")){
            callback.onError("Please enter your api key")
            return
        }
        callback.onProgressStart()

        val filePart = ProgressRequestBody(
            sourceFile,
            "image/png",
            object : ProgressRequestBody.ProgressListener {
                override fun fileTransferred(percentage: Float) {
                    callback.onProgressUpdate(percentage)
                }

            })

        // val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("source_image_file", sourceFile.name, filePart)
        val apiRequest: ApiRequest = RetrofitRequest.retrofitInstance!!.create(ApiRequest::class.java)

        apiRequest.upload(apiKey!!,body)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                callback.onProgressEnd()
                if (response.code()==200){
                    when {
                        response.body() != null -> {
                                val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                                callback.onSuccess(bmp)
                        }
                    }
                }else{
                    when {
                        response.code() != 401 -> callback.onError("Error Code ${response.code()}")
                        else -> callback.onError("Invalid api key")
                    }
                    when {
                        response.code() != 402 -> callback.onError("Error Code ${response.code()}")
                        else -> callback.onError("No credits remaining")
                    }
                    when {
                        response.code() != 429 -> callback.onError("Error Code ${response.code()}")
                        else -> callback.onError("Api rate limit crossed")
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
        fun onSuccess(response: Bitmap)
        fun onError(errors: String)
    }
}
