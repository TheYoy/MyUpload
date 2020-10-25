package com.example.myupload

import android.Manifest
import android.R.layout
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.widget.Toast
import com.codemobiles.cmuploadkotlin.CMCameraIntentHelperActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


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

        var r1 = 160
        var g1 = 161
        var b1 = 166
        var r2 = 167
//        val g2 = 157
//        val b2 = 124
        val r3 = 167
//        val g3 = 147
//        val b3 = 84
        val r4 = 165
//        val g4 = 139
//        val b4 = 55
        val r5 = 175
//        val g5 = 136
//        val b5 = 33
        val r6 = 167
//        val g6 = 124
//        val b6 = 20
        val r7 = 173
//        val g7 = 123
//        val b7 = 10

        var redColors = 0
        var greenColors = 0
        var blueColors = 0
        var pixelCount = 0

        for (y in 0 until _photoBitMap.getHeight()) {
            for (x in 0 until _photoBitMap.getWidth()) {
                val c: Int = _photoBitMap.getPixel(x, y)
                pixelCount++
                redColors += Color.red(c)
                greenColors += Color.green(c)
                blueColors += Color.blue(c)
            }
        }
        val red = redColors / pixelCount
        val green = greenColors / pixelCount
        val blue = blueColors / pixelCount

    //red
        var tr1  = 0
            if(red > r1) tr1 = red - r1
            else tr1 = r1 - red
        var tr2  = 0
            if(red > r2) tr2 = red - r2
            else tr2 = r2 - red
        var tr3  = 0
            if(red > r3) tr3 = red - r3
            else tr3 = r3 - red
        var tr4  = 0
            if(red > r4) tr4 = red - r4
            else tr4 = r4 - red
        var tr5  = 0
            if(red > r5) tr5 = red - r5
            else tr5 = r5 - red
        var tr6  = 0
            if(red > r6) tr6 = red - r6
            else tr6 = r6 - red
        var tr7  = 0
            if(red > r7) tr7 = red - r7
            else tr7 = r7 - red
    //red
        var tg1  = 0
            if(green > g1) tg1 = green - g1
            else tg1 = g1 - green
        var tb1  = 0
            if(blue > b1) tb1 = blue - b1
            else tb1 = b1 - blue
        //mText.text = tr1.toString() + tg1.toString() + tb1.toString()

        var ir = 1000
        var level = 0
        var s = "level 0"
        if(tr1 < ir) {
            ir = tr1
            level = 1
            s = "level 1"
        }
        if(tr2 < ir) {
            ir = tr2
            level = 2
            s = "level 2"
        }
        if(tr3 < ir) {
            ir = tr3
            level = 3
            s = "level 3"
        }
        if(tr4 < ir) {
            ir = tr4
            level = 4
            s = "level 4"
        }
        if(tr5 < ir) {
            ir = tr5
            level = 5
            s = "level 5"
        }
        if(tr6 < ir) {
            ir = tr6
            level = 6
            s = "level 6"
        }
        if(tr7 < ir) {
            ir = tr7
            level = 7
            s = "level 7"
        }
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(s.toString())
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("ตกลง") { dialog, _ ->
                //finish()
                dialog.cancel()
            }
            // negative button text and action
            .setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.cancel()
            }
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("ผลเปรียบเทียบสี")
        // show alert dialog
        alert.show()

    }
}
