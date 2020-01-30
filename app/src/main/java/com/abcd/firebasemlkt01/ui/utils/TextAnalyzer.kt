package com.abcd.firebasemlkt01.utils

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

/**
 * Created by JAI
 */
class TextAnalyzer constructor(context : Context) : ImageAnalysis.Analyzer {
    val cntxt =  context

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
        val mediaImage = imageProxy?.image
        // crop to detect from the left right top
        mediaImage?.cropRect = Rect(30,400,30,400)
        val imageRotation = degreesToFirebaseRotation(degrees)
        if (mediaImage != null) {
            // mode 1 -  SPARSE_MODEL text 2 for  DENSE_MODEL
//            val options = FirebaseVisionBarcodeDetectorOptions
////                .setModelType(1)
////                .setLanguageHints(mutableListOf("en"))
//                .build()

//            val options =   FirebaseVision.getInstance().cloudTextRecognizer
            // text recognizer detection
//            val detector = FirebaseVision.getInstance().cloudTextRecognizer
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            // firebase vision image
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)



            detector.processImage(image).addOnSuccessListener {
                Log.e("QrCodeAnalyzer", "succsess" + it.text)
                Toast.makeText(cntxt,it.text,Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Log.e("QrCodeAnalyzer", "something went wrong", it)

            }


        }
    }
}
