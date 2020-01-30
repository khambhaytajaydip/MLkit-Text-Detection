package com.abcd.firebasemlkt01.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.abcd.firebasemlkt01.R
import kotlinx.android.synthetic.main.activity_server_ocr.*
import kotlinx.android.synthetic.main.activity_server_ocr.textureView
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by JAI
 */

class TextDetectionActivity : AppCompatActivity() {

    companion object {
        var absoulatePath = ""
        const val REQUEST_CAMERA_PERMISSION = 10

    }

    private val executor = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_ocr)
        // Request camera permissions
        if (isCameraPermissionGranted(this@TextDetectionActivity)) {
            textureView.post { startCamera() }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }


    private fun startCamera() {

        CameraX.unbindAll()
        val previewConfig = PreviewConfig.Builder()
            // We want to show input from back camera of the device
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()
        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            textureView.surfaceTexture = previewOutput.surfaceTexture
        }

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setLensFacing(CameraX.LensFacing.BACK)
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                setTargetRotation(Surface.ROTATION_0)
                setTargetRotation(textureView.display.rotation)

            }.build()

        val capture = ImageCapture(imageCaptureConfig)

        // capture image
        btnCapture.setOnClickListener {
            // Create temporary file
            val fileName = System.currentTimeMillis().toString()
            val fileFormat = ".jpg"
            val imageFile = createTempFile(fileName, fileFormat)

            // Store captured image in the temporary file
            capture.takePicture(imageFile, object : ImageCapture.OnImageSavedListener {
                override fun onImageSaved(file: File) {
                    absoulatePath = file.absolutePath
                    finish()


                    // You may display the image for example using its path file.absolutePath
                }

                override fun onError(
                    useCaseError: ImageCapture.UseCaseError,
                    message: String,
                    cause: Throwable?
                ) {
                    // Display error message
                }
            })

        }
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, capture)
    }

    fun isCameraPermissionGranted(contect: Context): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(contect, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

}



