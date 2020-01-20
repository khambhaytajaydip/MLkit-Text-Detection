package com.abcd.firebasemlkt01.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.abcd.firebasemlkt01.R
import com.abcd.firebasemlkt01.baseDialog.BaseDialogPresenter
import com.abcd.firebasemlkt01.ui.presenter.MainPresenter
import com.abcd.firebasemlkt01.ui.view.MainView
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), MainView.UIView {

    private val CAMERA_REQUEST = 1001
    private lateinit var presenter: MainPresenter
    private lateinit var baseDialog: BaseDialogPresenter
    var currentPhotoPath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Initialize Presenter
        presenter = MainPresenter(this@MainActivity)
        //Initialize Progress Presenter
        baseDialog = BaseDialogPresenter(this@MainActivity)
        //Setting Listeners
        settingListeners()
    }

    override fun setTextView(analyzedText: String) {
        imgTxtView.text = analyzedText
    }

    override fun setBitmapOnImageView(bitmap: Bitmap) {
        capturedImage.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != CAMERA_REQUEST && resultCode != Activity.RESULT_OK) return
        Log.d("jai","path of the presenter :"+presenter.currentPhotoPath)

        when (requestCode) {

            CAMERA_REQUEST -> presenter.currentPhotoPath.let {
                Toast.makeText(applicationContext,currentPhotoPath,Toast.LENGTH_SHORT).show()
                // set capture image
                presenter.onGettingBitmapURIForCrop(Uri.fromFile(File(presenter.currentPhotoPath)))

                // set gallery image
//                presenter.onGettingBitmapURIForCrop(data?.data!!)

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->
                presenter.onGettingBitmapForImageView(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        CropImage.getActivityResult(data).uri
                    )
                )
        }
    }


    override fun setToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showDialog() {
        baseDialog.setAlertDialogView(true)
    }

    override fun dismissDialog() {
        baseDialog.setAlertDialogView(false)
    }

    private fun settingListeners() {
        btnOpenCamera.setOnClickListener {
            // gallary image
//            presenter.onCaptureClicked(CAMERA_REQUEST)
            // camera image capture
            presenter.onCaptureClicked(CAMERA_REQUEST)


        }
    }




}

