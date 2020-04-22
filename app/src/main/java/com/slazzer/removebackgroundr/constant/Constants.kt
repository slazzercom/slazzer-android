package com.anthempest.salesapp.constant

import android.content.Context
import android.graphics.Bitmap
import com.slazzer.removebackgroundr.R
import java.io.File
import java.io.FileOutputStream

class Constants {
    companion object {
        /**
         * GET YOUR API KEY FROM HERE -> https://slazzer.com/api
         */
        const val API_KEY = "=======YOUR-API-KEY========="
        fun getResizedImage(image: Bitmap, newFilePath: String) {
            var width = image.width.toFloat()
            var height = image.height.toFloat()
            var newWidth: Float
            var newHeight: Float
            if (height > width) {
                newWidth = 720f
                newHeight = height / width * newWidth
                width = newWidth
                height = newHeight
                if (newHeight > 970) {
                    newHeight = 970f
                    newWidth = width / height * newHeight
                }
            } else {
                newHeight = 970f
                newWidth = width / height * newHeight
                width = newWidth
                height = newHeight
                if (newWidth > 720) {
                    newWidth = 720f
                    newHeight = height / width * newWidth
                }
            }
            val reducedBitmap = Bitmap.createScaledBitmap(image, newWidth.toInt(), newHeight.toInt(), true)
            try {
                val file = File(newFilePath)
                val out = FileOutputStream(file)
                reducedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
            } catch (e: Exception) {
            }

        }
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }

    }

}