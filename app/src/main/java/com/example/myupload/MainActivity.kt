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
        mClean.setOnClickListener {
            pdelete()
        }
    }

    override fun onPhotoUriFound(_photoUri: Uri?, _photoBitMap: Bitmap?, _filePath: String?, _fileName: String) {
        super.onPhotoUriFound(_photoUri, _photoBitMap, _filePath, _fileName)

        mPreView.setImageBitmap(_photoBitMap)
        upload(_fileName,_photoBitMap!!)
    }
    private fun pdelete() {
        mPreView.setImageBitmap(null)
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
        // สตรีมไฟล์ภาพ Bitmap เป็น Byte[]
        // Compress Bitmap to Byte[] for upload
        val _baos = ByteArrayOutputStream()
        _photoBitMap.compress(Bitmap.CompressFormat.PNG, 0, _baos)

        //สร้างตัวแปรรับค่าสีจากรูปเปรียบเทียบโดยฟิกไว้
        val r1 = 160
        val g1 = 161
        val b1 = 166
        val r2 = 167
        val g2 = 157
        val b2 = 124
        val r3 = 167
        val g3 = 147
        val b3 = 84
        val r4 = 165
        val g4 = 139
        val b4 = 55
        val r5 = 175
        val g5 = 136
        val b5 = 33
        val r6 = 167
        val g6 = 124
        val b6 = 20
        val r7 = 173
        val g7 = 123
        val b7 = 10

        //ฟังชั่นหาค่า RGB จากรูปที่ถ่ายหรืออัพมา
        var redColors = 0
        var greenColors = 0
        var blueColors = 0
        var pixelCount = 0

        // โดยทำการวนลูปหาพื้นที่ส่วนสูง และความกว้าง เพื่อหา Pixelของสี จากแกน X Y
        // นำ Pixel ไปแปลงเป็น RGB
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

    //ฟังชั่นนำค่าสีมาคำนณวนโดยเช็คค่ามากลบค่าน้อย เพื่อหาผลบวก
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
    //green
        var tg1  = 0
            if(green > g1) tg1 = green - g1
            else tg1 = g1 - green
        var tg2  = 0
            if(green > g2) tg2 = green - g2
            else tg2 = g2 - green
        var tg3  = 0
            if(green > g3) tg3 = green - g3
            else tg3 = g3 - green
        var tg4  = 0
            if(green > g4) tg4 = green - g4
            else tg4 = g4 - green
        var tg5  = 0
            if(green > g5) tg5 = green - g5
            else tg5 = g5 - green
        var tg6  = 0
            if(green > g6) tg6 = green - g6
            else tg6 = g6 - green
        var tg7  = 0
            if(green > g7) tg7 = green - g7
            else tg7 = g7 - green
        //green

        //blue
        var tb1  = 0
            if(blue > b1) tb1 = blue - b1
            else tb1 = b1 - blue
        var tb2  = 0
            if(blue > b2) tb2 = blue - b2
            else tb2 = b2 - blue
        var tb3  = 0
            if(blue > b3) tb3 = blue - b3
            else tb3 = b3 - blue
        var tb4  = 0
            if(blue > b4) tb4 = blue - g4
            else tb4 = b4 - blue
        var tb5  = 0
            if(blue > b5) tb5 = blue - b5
            else tb5 = b5 - blue
        var tb6  = 0
            if(blue > b6) tb6 = blue - b6
            else tb6 = b6 - blue
        var tb7  = 0
            if(blue > b7) tb7 = blue - b7
            else tb7 = b7 - blue
        //blue


        //mText.text = tr1.toString() + tg1.toString() + tb1.toString()

        //บังชั่นเช็คค่าที่ใกล้เคียงที่สุด คำณวนโดยหาค่าที่น้อยสุดจากการลบกันเมื่อพบค่าที่น้อยที่สุด จะนำเข้าไปเก็บในตัวแปร จออกลูปของการทำงาน
        var ir = 1000
        var ig = 1000
        var ib = 1000
        var level = 0
        var s = "level 0"
        if(tr1 < ir && tg1 < ig && tb1 < ib) {
            ir = tr1
            ig = tg1
            ib = tb1
            level = 1
            s = "level 1"
        }
        if(tr2 < ir && tg2 < ig && tb2 < ib) {
            ir = tr2
            ig = tg2
            ib = tb2
            level = 2
            s = "level 2"
        }
        if(tr3 < ir && tg3 < ig && tb3 < ib) {
            ir = tr3
            ig = tg3
            ib = tb3
            level = 3
            s = "level 3"
        }
        if(tr4 < ir && tg4 < ig && tb4 < ib) {
            ir = tr4
            ig = tg4
            ib = tb4
            level = 4
            s = "level 4"
        }
        if(tr5 < ir && tg5 < ig && tb5 < ib) {
            ir = tr5
            ig = tg5
            ib = tb5
            level = 5
            s = "level 5"
        }
        if(tr6 < ir && tg6 < ig && tb6 < ib) {
            ir = tr6
            ig = tg6
            ib = tb6
            level = 6
            s = "level 6"
        }
        if(tr7 < ir && tg7 < ig && tb7 < ib) {
            ir = tr7
            ig = tg7
            ib = tb7
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
