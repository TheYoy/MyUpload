package com.example.myupload

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.codemobiles.cmuploadkotlin.CMCameraIntentHelperActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : CMCameraIntentHelperActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkRuntimePermission()
        setUpEventWidget()
    }

    private fun setUpEventWidget() {
        mCamera.setOnClickListener {

            startCameraIntent(500) // - 1 not zip
        }
        mGallery.setOnClickListener {
            startGalleryIntent(500)
        }
    }

    override fun onPhotoUriFound(_photoUri: Uri?, _photoBitMap: Bitmap?, _filePath: String?, _fileName: String) {
        super.onPhotoUriFound(_photoUri, _photoBitMap, _filePath, _fileName)

        mPreView.setImageBitmap(_photoBitMap)
        upload(_fileName,_photoBitMap!!)
    }

    private fun checkRuntimePermission() {

        val _permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        Dexter.withActivity(this)
            .withPermissions(*_permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show()
                    } else {
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun upload(_fileName: String, _photoBitMap: Bitmap) {

        // Compress Bitmap to Byte[] for upload
        val _baos = ByteArrayOutputStream()
        _photoBitMap.compress(Bitmap.CompressFormat.PNG, 0, _baos)


        val _reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), _baos.toByteArray()) //multipart/form-data //key from server
        val _bodyImage = MultipartBody.Part.createFormData("userfile", _fileName, _reqFile)

        // Sent Data to server (optional)
        val _username = "admin"
        val _password = "i love codemobiles"
        val _bodyUsername = RequestBody.create(MediaType.parse("text/plain"), _username)
        val _bodyPassword = RequestBody.create(MediaType.parse("text/plain"), _password)

        val _call = ApiInterface.getClient().postImageNodeJS(_bodyImage, _bodyUsername, _bodyPassword)
        _call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Toast.makeText(this@MainActivity, response.body()!!.string(), Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Log.e("codemobiles_restful", e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("codemobiles_restful", t.message.toString())
            }
        })


    }
}
