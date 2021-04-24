package com.slazzer.bgremover.network

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Source
import okio.source
import java.io.File
import java.io.IOException

class ProgressRequestBody(private val inputFile: File,
                          private val contentType: String,
                          private val progressListner: ProgressListener
) : RequestBody() {

    override fun contentLength(): Long {
        return inputFile.length()
    }

    override fun contentType(): MediaType? {
        return contentType.toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        var source: Source? = null
        try {
            source = inputFile.source()
            var total: Long = 0
            var read: Long = -1
            while (run {
                    read = source.read(
                        sink.buffer,
                        SEGMENT_SIZE
                    )
                    read
                } != -1L) {
                total += read
                sink.flush()
                val percentage = (total * 100f) / contentLength()
                this.progressListner.fileTransferred(percentage)
            }

        } finally {
            source?.close()
        }
    }

    interface ProgressListener {
        fun fileTransferred(percentage: Float)
    }

    companion object {
        private const val SEGMENT_SIZE = 2048L // okio.Segment.SIZE
    }

}