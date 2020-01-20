package com.abcd.firebasemlkt01.ui.presenter
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.abcd.firebasemlkt01.R
import com.abcd.firebasemlkt01.ui.MainActivity
import com.abcd.firebasemlkt01.ui.view.MainView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainPresenter(private val mainActivity: MainActivity) : MainView.PresenterView {
    private var permissionFlag = false
    var currentPhotoPath = ""

    init {
        onGettingPermission()
    }

    override fun grantPermission(): Boolean {
        return permissionFlag
    }

    override fun onCaptureClicked(CAMERA_REQUEST: Int) {
        if (!permissionFlag) {
            mainActivity.setToast(mainActivity.getString(R.string.denied))
            return
        }

        when (CAMERA_REQUEST) {
            CAMERA_REQUEST -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.resolveActivity(mainActivity.packageManager).also {
                    // Create the File where the photo should go
                    var photoFile: File? = try {
                        createImageFile(mainActivity)
                        // set rotetion
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            mainActivity.baseContext,
                            mainActivity.packageName + ".provider",
                            it
                        )
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        mainActivity.startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    }
                }


            }
            else -> {
                val gallary =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                mainActivity.startActivityForResult(gallary, CAMERA_REQUEST)
            }
        }

        "c"
    }

    @Throws(IOException::class)
    fun createImageFile(context: Activity): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath

        }
    }


    override fun onGettingBitmapForImageView(bitmap: Bitmap) {
        mainActivity.showDialog()
        mainActivity.setBitmapOnImageView(bitmap)
        onGettingVisionBitmapAnalysis(bitmap)
    }

    override fun onGettingBitmapURIForCrop(bitmapURI: Uri) {
        CropImage.activity(bitmapURI)
            .setBackgroundColor(R.color.crop_shade)
            .setActivityTitle(R.string.cropping.toString())
            .start(mainActivity)

    }

    private fun onGettingVisionAnalysisText(bitmap: Bitmap, visionText: FirebaseVisionText) {
        val blocks = visionText.textBlocks
        mainActivity.setTextView(
            when {
                blocks.isEmpty() -> "No Text Found!!"
                else -> visionText.text
            }
        )

        if (blocks.isEmpty()) {
            mainActivity.dismissDialog()
            return
        }

        onGettingLabelFromImage(bitmap, blocks)

    }

    private fun onGettingVisionBitmapAnalysis(bitmap: Bitmap) {

        val fbVisionImg = FirebaseVisionImage.fromBitmap(bitmap)
        val fbVisionTxtDetect = FirebaseVision.getInstance().onDeviceTextRecognizer
        fbVisionTxtDetect.processImage(fbVisionImg)
            .addOnSuccessListener {
                onGettingVisionAnalysisText(bitmap, it)
            }
            .addOnFailureListener {
                when {
                    it.printStackTrace().toString().equals(R.string.model_download_warning.toString()) -> {
                        onGettingVisionBitmapAnalysis(bitmap)
                    }
                    else -> {
                        mainActivity.dismissDialog()
                        it.printStackTrace()
                    }
                }

            }
    }

    private fun onGettingLabelFromImage(
        bitmap: Bitmap,
        blocks: List<FirebaseVisionText.TextBlock>
    ) {

        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val graphics = onGettingGraphics()

        for (i in blocks.indices) {
            val lines: List<FirebaseVisionText.Line> = blocks[i].lines
            for (j in lines.indices) {
                val elements: List<FirebaseVisionText.Element> = lines[j].elements
                for (k in elements.indices) {
                    canvas.drawRect(elements[k].boundingBox, graphics.first)
                }
            }
        }
        mainActivity.setBitmapOnImageView(mutableBitmap)
        mainActivity.dismissDialog()

    }

    private fun onGettingPermission() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Permissions.check(
            mainActivity/*context*/,
            permissions,
            null/*options*/,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    permissionFlag = true
                }
            })
    }

    private fun onGettingGraphics(): Pair<Paint, Paint> {
        val rectPaint = Paint()
        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 4F
        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 40F
        return Pair(rectPaint, textPaint)
    }

}