package com.codemobiles.cmuploadkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A helper Activity to start the camera activity and retrieve the location
 * and orientation of the photo.
 *
 * @author Ralf Gehrer <ralf></ralf>@ecotastic.de>
 */
open class CMCameraIntentHelperActivity : AppCompatActivity() {
    private var mPhotoBitMap: Bitmap? = null

    // By using this method get the Uri of Internal/External Storage for Media
    private val uri: Uri
        get() {
            val state = Environment.getExternalStorageState()
            return if (!state.equals(Environment.MEDIA_MOUNTED, ignoreCase = true)) MediaStore.Images.Media.INTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        }

    val randomFileName: String
        get() {
            val _df = android.text.format.DateFormat.format("MMddyyyyhhmmss",
                java.util.Date()).toString()
            val r = Random()
            val random = Math.abs(r.nextInt() % 100)
            return String.format("%d%s.jpg", random, _df)
        }

    /**
     * Saves the current state of the activity.
     */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        if (dateCameraIntentStarted != null) {
            savedInstanceState.putString(DATE_CAMERA_INTENT_STARTED_STATE, CMDateParser.dateToString(dateCameraIntentStarted))
        }
        if (preDefinedCameraUri != null) {
            savedInstanceState.putString(CAMERA_PIC_URI_STATE, preDefinedCameraUri!!.toString())
        }
        if (photoUri != null) {
            savedInstanceState.putString(PHOTO_URI_STATE, photoUri!!.toString())
        }
        savedInstanceState.putInt(ROTATE_X_DEGREES_STATE, rotateXDegrees)
    }

    /**
     * Reinitializes a saved state of the activity.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(DATE_CAMERA_INTENT_STARTED_STATE)) {
            dateCameraIntentStarted = CMDateParser.stringToDate(savedInstanceState.getString(DATE_CAMERA_INTENT_STARTED_STATE))
        }
        if (savedInstanceState.containsKey(CAMERA_PIC_URI_STATE)) {
            preDefinedCameraUri = Uri.parse(savedInstanceState.getString(CAMERA_PIC_URI_STATE))
        }
        if (savedInstanceState.containsKey(PHOTO_URI_STATE)) {
            photoUri = Uri.parse(savedInstanceState.getString(PHOTO_URI_STATE))
        }
        rotateXDegrees = savedInstanceState.getInt(ROTATE_X_DEGREES_STATE)
    }

    /**
     * Starts the camera intent depending on the device configuration.
     *
     *
     * **for Samsung and Sony devices:**
     * We call the camera activity with the method call to startActivityForResult. We only set the constant CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE. We do NOT set any other intent extras.
     *
     *
     * **for all other devices:**
     * We call the camera activity with the method call to startActivityForResult as previously. This time, however, we additionally set the intent extra MediaStore.EXTRA_OUTPUT and provide an URI, where we want the image to be stored.
     *
     *
     * In both cases we remember the time the camera activity was started.
     */
    protected fun startCameraIntent(maxPixel: Int) {
        mMaxPixel = maxPixel
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            try {
                // NOTE: Do NOT SET: intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri)
                // on Samsung Galaxy S2/S3/.. for the following reasons:
                // 1.) it will break the correct picture orientation
                // 2.) the photo will be stored in two locations (the given path and, additionally, in the MediaStore)
                val manufacturer = android.os.Build.MANUFACTURER.toLowerCase(Locale.ENGLISH)
                val model = android.os.Build.MODEL.toLowerCase(Locale.ENGLISH)
                val buildType = android.os.Build.TYPE.toLowerCase(Locale.ENGLISH)
                val buildDevice = android.os.Build.DEVICE.toLowerCase(Locale.ENGLISH)
                val buildId = android.os.Build.ID.toLowerCase(Locale.ENGLISH)
                //				String sdkVersion = android.os.Build.VERSION.RELEASE.toLowerCase(Locale.ENGLISH);

                var setPreDefinedCameraUri = false
                if (!manufacturer.contains("samsung") && !manufacturer.contains("sony")) {
                    setPreDefinedCameraUri = true
                }
                if (manufacturer.contains("samsung") && model.contains("galaxy nexus")) { //TESTED
                    setPreDefinedCameraUri = true
                }
                if (manufacturer.contains("samsung") && model.contains("gt-n7000") && buildId.contains("imm76l")) { //TESTED
                    setPreDefinedCameraUri = true
                }

                if (buildType.contains("userdebug") && buildDevice.contains("ariesve")) {  //TESTED
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("crespo")) {   //TESTED
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9100")) { //TESTED
                    setPreDefinedCameraUri = true
                }

                ///////////////////////////////////////////////////////////////////////////
                // TEST
                if (manufacturer.contains("samsung")) { //T-Mobile LTE enabled Samsung S3
                    setPreDefinedCameraUri = true
                    rotateXDegrees = 90
                }
                if (buildDevice.contains("cooper")) {
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t0lte")) {
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("kot49h")) {
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t03g")) {
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9300")) {
                    setPreDefinedCameraUri = true
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9195")) {
                    setPreDefinedCameraUri = true
                }
                if (manufacturer.contains("asus")) {
                    setPreDefinedCameraUri = true
                    rotateXDegrees = 0

                }

                ///////////////////////////////////////////////////////////////////////////


                dateCameraIntentStarted = Date()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (setPreDefinedCameraUri) {
                    val filename = System.currentTimeMillis().toString() + ".jpg"
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, filename)
                    preDefinedCameraUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, preDefinedCameraUri)
                }
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                logException(e)
                onCouldNotTakePhoto()
            }

        } else {
            onSdCardNotMounted()
        }
    }


    protected fun startGalleryIntent(maxPixel: Int) {
        mMaxPixel = maxPixel
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            try {
                // NOTE: Do NOT SET: intent.putExtra(MediaStore.EXTRA_OUTPUT,
                // cameraPicUri)
                // on Samsung Galaxy S2/S3/.. for the following reasons:
                // 1.) it will break the correct picture orientation
                // 2.) the photo will be stored in two locations (the given path
                // and, additionally, in the MediaStore)
                val manufacturer = android.os.Build.MANUFACTURER
                    .toLowerCase(Locale.ENGLISH)
                Log.i("ploy", manufacturer)

                val model = android.os.Build.MODEL
                    .toLowerCase(Locale.ENGLISH)
                val buildType = android.os.Build.TYPE
                    .toLowerCase(Locale.ENGLISH)
                val buildDevice = android.os.Build.DEVICE
                    .toLowerCase(Locale.ENGLISH)
                val buildId = android.os.Build.ID
                    .toLowerCase(Locale.ENGLISH)
                val sdkVersion = android.os.Build.VERSION.RELEASE
                    .toLowerCase(Locale.ENGLISH)
                if (manufacturer.contains("samsung")) { // TESTED
                    rotateXDegrees = 90
                } else if (manufacturer.contains("asus")) {
                    rotateXDegrees = 0

                }


            } catch (e: Exception) {

            }

        }

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            GALLERY_IMAGE_ACTIVITY_REQUEST_CODE)

    }


    /**
     * Receives all activity results and triggers onCameraIntentResult if the
     * requestCode matches.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                onCameraIntentResult(requestCode, resultCode, intent)
            }
            GALLERY_IMAGE_ACTIVITY_REQUEST_CODE -> {
                try {
                    photoUri = intent!!.data
                    val filePath = getImageFilePath(photoUri, this)
                    mPhotoBitMap = CMBitmapHelper.readBitmap(this, photoUri)

                    if (mPhotoBitMap != null && mMaxPixel != -1) {
                        mPhotoBitMap = CMBitmapHelper.shrinkBitmap(mPhotoBitMap!!, mMaxPixel,
                            rotateXDegrees)
                    }
                    onPhotoUriFound(photoUri, mPhotoBitMap, filePath, getFilename(filePath))
                } catch (e: Exception) {

                }

            }
        }
    }


    /**
     * On camera activity result, we try to locate the photo.
     *
     *
     * **Mediastore:**
     * First, we try to read the photo being captured from the MediaStore. Using a ContentResolver on the MediaStore content, we retrieve the latest image being taken, as well as its orientation property and its timestamp. If we find an image and it was not taken before the camera intent was called, it is the image we were looking for. Otherwise, we dismiss the result and try one of the following approaches.
     * **Intent extra:**
     * Second, we try to get an image Uri from intent.getData() of the returning intent. If this is not successful either, we continue with step 3.
     * **Default photo Uri:**
     * If all of the above mentioned steps did not work, we use the image Uri we passed to the camera activity.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected fun onCameraIntentResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var myCursor: Cursor? = null
            var dateOfPicture: Date? = null
            try {
                // Create a Cursor to obtain the file Path for the large image
                val largeFileProjection = arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN)
                val largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC"
                myCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    largeFileProjection, null, null,
                    largeFileSort)
                myCursor!!.moveToFirst()
                if (!myCursor.isAfterLast) {
                    // This will actually give you the file path location of the image.
                    val largeImagePath = myCursor.getString(myCursor
                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                    photoUri = Uri.fromFile(File(largeImagePath))
                    if (photoUri != null) {
                        dateOfPicture = Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)))
                        if (dateOfPicture != null && dateOfPicture.after(dateCameraIntentStarted!!)) {
                            rotateXDegrees = myCursor.getInt(myCursor
                                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION))
                        } else {
                            photoUri = null
                        }
                    }
                    if (myCursor.moveToNext() && !myCursor.isAfterLast) {
                        val largeImagePath3rdLocation = myCursor.getString(myCursor
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                        val dateOfPicture3rdLocation = Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)))
                        if (dateOfPicture3rdLocation != null && dateOfPicture3rdLocation.after(dateCameraIntentStarted!!)) {
                            photoUriIn3rdLocation = Uri.fromFile(File(largeImagePath3rdLocation))
                        }
                    }
                }
            } catch (e: Exception) {
                logException(e)
            } finally {
                if (myCursor != null && !myCursor.isClosed) {
                    myCursor.close()
                }
            }

            if (photoUri == null) {
                try {
                    photoUri = intent!!.data
                } catch (e: Exception) {
                }

            }

            if (photoUri == null) {
                photoUri = preDefinedCameraUri
            }

            try {
                if (photoUri != null && File(photoUri!!.path).length() <= 0) {
                    if (preDefinedCameraUri != null) {
                        val tempUri = photoUri
                        photoUri = preDefinedCameraUri
                        preDefinedCameraUri = tempUri
                    }
                }
            } catch (e: Exception) {
            }

            photoUri = getFileUriFromContentUri(photoUri)
            preDefinedCameraUri = getFileUriFromContentUri(preDefinedCameraUri)
            try {
                if (photoUriIn3rdLocation != null) {
                    if (photoUriIn3rdLocation == photoUri || photoUriIn3rdLocation == preDefinedCameraUri) {
                        photoUriIn3rdLocation = null
                    } else {
                        photoUriIn3rdLocation = getFileUriFromContentUri(photoUriIn3rdLocation)
                    }
                }
            } catch (e: Exception) {
            }

            if (photoUri != null) {
                val filePath = getImageFilePath(photoUri, this)
                mPhotoBitMap = CMBitmapHelper.readBitmap(this, photoUri)
                mPhotoBitMap = CMBitmapHelper.readBitmap(this, photoUri)

                if (mPhotoBitMap != null && mMaxPixel != -1) {
                    mPhotoBitMap = CMBitmapHelper.shrinkBitmap(mPhotoBitMap!!, mMaxPixel,
                        rotateXDegrees)
                }
                onPhotoUriFound(photoUri, mPhotoBitMap, filePath, getFilename(filePath))
            } else {
                onPhotoUriNotFound()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            onCanceled()
        } else {
            onCanceled()
        }
    }

    /**
     * Being called if the photo could be located. The photo's Uri
     * and its orientation could be retrieved.
     */
    protected open fun onPhotoUriFound(_photoUri: Uri?, _photoBitMap: Bitmap?, _filePath: String?, _fileName: String) {
        logMessage("Your photo is stored under: " + _photoUri!!.toString())
    }

    /**
     * Being called if the photo could not be located (Uri == null).
     */
    protected fun onPhotoUriNotFound() {
        logMessage("Could not find a photoUri that is != null")
    }

    /**
     * Being called if the camera intent could not be started or something else went wrong.
     */
    protected fun onCouldNotTakePhoto() {
        Toast.makeText(applicationContext, "Could not take photo", Toast.LENGTH_LONG).show()
    }

    /**
     * Being called if the SD card (or the internal mass storage respectively) is not mounted.
     */
    protected fun onSdCardNotMounted() {
        Toast.makeText(applicationContext, "Could not mount sdcard", Toast.LENGTH_LONG).show()
    }

    /**
     * Being called if the camera intent was canceled.
     */
    protected fun onCanceled() {
        logMessage("Camera Intent was canceled")
    }

    /**
     * Logs the passed exception.
     *
     * @param exception
     */
    protected fun logException(exception: Exception) {
        logMessage(exception.toString())
    }

    /**
     * Logs the passed exception messages.
     *
     * @param exceptionMessage
     */
    protected fun logMessage(exceptionMessage: String) {
        Log.d(javaClass.name, exceptionMessage)
    }

    /**
     * Given an Uri that is a content Uri (e.g.
     * content://media/external/images/media/1884) this function returns the
     * respective file Uri, that is e.g. file://media/external/DCIM/abc.jpg
     *
     * @param cameraPicUri
     * @return Uri
     */
    private fun getFileUriFromContentUri(cameraPicUri: Uri?): Uri? {
        try {
            if (cameraPicUri != null && cameraPicUri.toString().startsWith("content")) {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(cameraPicUri, proj, null, null, null)
                cursor!!.moveToFirst()
                // This will actually give you the file path location of the image.
                val largeImagePath = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                return Uri.fromFile(File(largeImagePath))
            }
            return cameraPicUri
        } catch (e: Exception) {
            return cameraPicUri
        }

    }

    class CMBitmapHelper {


        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        /**
         * Converts a byteArray to a Bitmap object
         *
         * @param byteArray
         * @return Bitmap
         */
        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            return if (byteArray == null) {
                null
            } else {
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }

        /**
         * Shrinks and a passed Bitmap.
         *
         * @param bm
         * @param maxLengthOfEdge
         * @return Bitmap
         */
        fun shrinkBitmap(bm: Bitmap, maxLengthOfEdge: Int): Bitmap {
            return shrinkBitmap(bm, maxLengthOfEdge, 0)
        }

        /**
         * Clears all Bitmap data, that is, recycles the Bitmap and
         * triggers the garbage collection.
         *
         * @param bm
         */
        fun clearBitmap(bm: Bitmap) {
            bm.recycle()
            System.gc()
        }


        /**
         * Deletes an image given its Uri and refreshes the gallery thumbnails.
         *
         * @param cameraPicUri
         * @param context
         * @return true if it was deleted successfully, false otherwise.
         */
        fun deleteImageWithUriIfExists(cameraPicUri: Uri?, context: Context): Boolean {
            try {
                if (cameraPicUri != null) {
                    val fdelete = File(cameraPicUri.path)
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            refreshGalleryImages(context, fdelete)
                            return true
                        }
                    }
                }
            } catch (e: Exception) {
            }

            return false
        }

        /**
         * Forces the Android gallery to  refresh its thumbnail images.
         *
         * @param context
         * @param fdelete
         */
        private fun refreshGalleryImages(context: Context, fdelete: File) {
            try {
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
            } catch (e1: Exception) {
                try {
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(fdelete)
                    mediaScanIntent.data = contentUri
                    context.sendBroadcast(mediaScanIntent)
                } catch (e2: Exception) {
                }

            }

        }

        companion object {

            /**
             * Shrinks and rotates (if necessary) a passed Bitmap.
             *
             * @param bm
             * @param maxLengthOfEdge
             * @param rotateXDegree
             * @return Bitmap
             */
            fun shrinkBitmap(bm: Bitmap, maxLengthOfEdge: Int, rotateXDegree: Int): Bitmap {
                var bm = bm
                if (maxLengthOfEdge > bm.width && maxLengthOfEdge > bm.height) {
                    return bm
                } else {
                    // shrink image
                    var scale = 1.0.toFloat()
                    if (bm.height > bm.width) {
                        scale = maxLengthOfEdge.toFloat() / bm.height
                    } else {
                        scale = maxLengthOfEdge.toFloat() / bm.width
                    }
                    // CREATE A MATRIX FOR THE MANIPULATION
                    var matrix: Matrix? = Matrix()
                    // RESIZE THE BIT MAP
                    matrix!!.postScale(scale, scale)
                    matrix.postRotate(rotateXDegree.toFloat())

                    // RECREATE THE NEW BITMAP
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height,
                        matrix, false)

                    matrix = null
                    System.gc()

                    return bm
                }
            }

            /**
             * Reads a Bitmap from an Uri.
             *
             * @param context
             * @param selectedImage
             * @return Bitmap
             */
            fun readBitmap(context: Context, selectedImage: Uri?): Bitmap? {
                var bm: Bitmap? = null
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                options.inScaled = false
                //      options.inSampleSize = 3;
                var fileDescriptor: AssetFileDescriptor? = null
                try {
                    fileDescriptor = context.contentResolver.openAssetFileDescriptor(selectedImage!!, "r")
                } catch (e: FileNotFoundException) {
                    return null
                } finally {
                    try {
                        bm = BitmapFactory.decodeFileDescriptor(
                            fileDescriptor!!.fileDescriptor, null, options)
                        fileDescriptor.close()
                    } catch (e: IOException) {
                        return null
                    }

                }
                return bm
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    object CMDateParser {
        //ISO 8601 international standard date format
        val dateFormat = "yyyy-MM-dd HH:mm:ss.SSSZ"

        val utc = TimeZone.getTimeZone("UTC")

        /**
         * Converts a Date object to a string representation.
         *
         * @param date
         * @return date as String
         */
        fun dateToString(date: Date?): String? {
            if (date == null) {
                return null
            } else {
                val df = SimpleDateFormat(dateFormat)
                df.timeZone = utc
                return df.format(date)
            }
        }

        /**
         * Converts a string representation of a date to its Date object.
         *
         * @param dateAsString
         * @return Date
         */
        fun stringToDate(dateAsString: String?): Date? {
            try {
                val df = SimpleDateFormat(dateFormat)
                df.timeZone = utc
                return df.parse(dateAsString)
            } catch (e: ParseException) {
                return null
            } catch (e: NullPointerException) {
                return null
            }

        }
    }

    private fun getFilename(_filePath: String?): String {
        val _file = File(_filePath!!)
        return _file.name
    }

    private fun getImageFilePath(originalUri: Uri?, activity: Activity): String? {
        // get file path in string
        var selectedImagePath: String? = null
        val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
        val cursor = activity.managedQuery(originalUri, projection, null, null, null)
        if (cursor != null) {
            val index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            selectedImagePath = cursor.getString(index)
            if (selectedImagePath == null) {

                val id = originalUri!!.lastPathSegment.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val imageColumns = arrayOf(MediaStore.Images.Media.DATA)
                val imageOrderBy: String? = null

                val uri = uri

                val imageCursor = activity.managedQuery(uri, imageColumns,
                    MediaStore.Images.Media._ID + "=" + id, null,
                    imageOrderBy)

                if (imageCursor.moveToFirst()) {
                    selectedImagePath = imageCursor.getString(imageCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA))
                }

                Log.e("path", selectedImagePath) // use selectedImagePath
            }
        } else {
            return File(originalUri!!.path).absolutePath
        }
        return selectedImagePath
    }

    companion object {

        private val DATE_CAMERA_INTENT_STARTED_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.dateCameraIntentStarted"
        private val CAMERA_PIC_URI_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.CAMERA_PIC_URI_STATE"
        private val PHOTO_URI_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.PHOTO_URI_STATE"
        private val ROTATE_X_DEGREES_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.ROTATE_X_DEGREES_STATE"


        /**
         * Date and time the camera intent was started.
         */
        protected var dateCameraIntentStarted: Date? = null
        /**
         * Default location where we want the photo to be ideally stored.
         */
        protected var preDefinedCameraUri: Uri? = null
        /**
         * Potential 3rd location of photo data.
         */
        protected var photoUriIn3rdLocation: Uri? = null
        /**
         * Retrieved location of the photo.
         */
        protected var photoUri: Uri? = null
        /**
         * Orientation of the retrieved photo.
         */
        protected var rotateXDegrees = 0

        protected var mMaxPixel = 0


        private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100
        private val GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 101
    }
}