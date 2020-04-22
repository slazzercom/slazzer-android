package com.slazzer.removebackgroundr

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.anthempest.salesapp.constant.Constants
import com.slazzer.bgremover.Slazzer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var sourceImageView: ImageView? = null
    private var outPutImage: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var consOutputImage: ConstraintLayout? = null
    private var mUri: Uri? = null
    private var resizedImgeFile: File? = null
    private lateinit var btnCapture: Button
    private lateinit var btnChoose : Button
    private val operationCaptureImage = 1
    private val operationChooseImage = 2
    private val cameraPermissionCode = 4
    private val galleryPermissionCode = 5
    private lateinit var capturedImage:File
    private val permissionStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeWidgets()
        btnCapture.setOnClickListener{
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionCode)
            }
            else{
                capturePhoto()
            }

        }
        btnChoose.setOnClickListener{
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissionStorage, galleryPermissionCode)
            }
            else{
                openGallery()
            }
        }
    }
    private fun initializeWidgets() {
        btnCapture = findViewById(R.id.btnCapture)
        btnChoose = findViewById(R.id.btnChoose)
        sourceImageView = findViewById(R.id.sourceImage)
        progressBar = findViewById(R.id.progressBar)
        consOutputImage = findViewById(R.id.consOutputImage)
        outPutImage = findViewById(R.id.outPutImage)
        capturedImage= File(Constants.getOutputDirectory(this), System.currentTimeMillis().toString()+"Slazzer_Photo.jpg")

    }

    private fun show(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    private fun capturePhoto(){
        if(capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "com.slazzer.removebackgroundr.fileprovider",
                capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, operationCaptureImage)
    }
    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, operationChooseImage)
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }
    private fun imagePathHandler(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri?.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse(
                    "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri?.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri?.scheme, ignoreCase = true)){
            imagePath = uri?.path
        }
        renderImage(imagePath)
    }
    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)

            sourceImageView?.setImageBitmap(bitmap)
            uploadImage(imagePath)
        }
        else {
            show(getString(R.string.path_error))
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            galleryPermissionCode -> {
                if (grantedResults.isNotEmpty() && grantedResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    openGallery()
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
            }
            cameraPermissionCode -> {
                if (grantedResults.isNotEmpty() && grantedResults[0] == PackageManager.PERMISSION_GRANTED){
                    capturePhoto()
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            operationCaptureImage ->
                if (resultCode == Activity.RESULT_OK) {
                    val resizePath = resizeImage(capturedImage.absolutePath)
                    uploadImage(resizePath!!)
                }
            operationChooseImage ->
                if (resultCode == Activity.RESULT_OK) {
                    imagePathHandler(data)
                }
        }
    }

    private fun uploadImage(inputFilePath: String) {
        val inputFile = File(inputFilePath)
        Slazzer.from(inputFile,object :Slazzer.ResponseCallback{
            override fun onProgressStart() {
                this@MainActivity.progressBar?.visibility= View.VISIBLE
                tvOutPut?.visibility= View.VISIBLE
                tvOutPut.text = resources.getString(R.string.please_wait)
            }

            override fun onProgressEnd() {
                this@MainActivity.progressBar?.visibility= View.GONE
            }

            override fun onProgressUpdate(percentage: Float) {
                runOnUiThread {
                    if (percentage.toInt()>=100){
                        tvOutPut.text = getString(R.string.file_processing)
                    }else{
                        //"Uploading... ${percentage.toInt()}%"
                        tvOutPut.text =getString(R.string.uploading,percentage.toInt())
                    }
                    progressBar?.progress = percentage.toInt()
                }

            }

            override fun onSuccess(response: String) {
                val jsonObject=JSONObject(response)
                this@MainActivity.progressBar?.visibility= View.GONE
                if (jsonObject.optBoolean("status")){
                    consOutputImage?.visibility=View.VISIBLE
                    tvOutPut.text = resources.getString(R.string.out_put_image)
                    Picasso.get().load(jsonObject.optString("output_image_url")).into(outPutImage)
                }else{
                    show(jsonObject.optString("message"))
                }
            }

            override fun onError(errors: String) {
                show(errors)
                this@MainActivity.progressBar?.visibility= View.GONE
                tvOutPut?.visibility= View.GONE
            }

        })
    }

    private fun resizeImage(selectedImage:String):String? {
        val pictureFile: File?
        val resizeImageFile = File(Constants.getOutputDirectory(this), "resize-img-"+ System.currentTimeMillis() / 100 + ".jpg")
        val resizeImagePath: String = resizeImageFile.toString()
        pictureFile = File(selectedImage)
        if (pictureFile.exists()) {
            val imageBitmap = BitmapFactory.decodeFile(pictureFile.absolutePath)
            Constants.getResizedImage(imageBitmap, resizeImagePath)
            resizedImgeFile = File(resizeImagePath)
            sourceImageView!!.setImageBitmap(imageBitmap)
            return resizeImagePath
        }
        return null
    }
}
