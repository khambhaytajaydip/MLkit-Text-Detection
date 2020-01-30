package com.abcd.firebasemlkt01.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abcd.firebasemlkt01.R
import com.abcd.firebasemlkt01.baseDialog.BaseDialogPresenter
import com.abcd.firebasemlkt01.ui.presenter.MainPresenter
import com.abcd.firebasemlkt01.ui.utils.CurvedBottomNavigationView
import com.abcd.firebasemlkt01.ui.view.MainView
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


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
        Log.d("jai", "path of the presenter :" + presenter.currentPhotoPath)

        when (requestCode) {

            CAMERA_REQUEST -> presenter.currentPhotoPath.let {
                Toast.makeText(applicationContext, currentPhotoPath, Toast.LENGTH_SHORT).show()
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
//            presenter.onCaptureClicked(CAMERA_REQUEST)
            startActivity(Intent(this@MainActivity, TextDetectionActivity::class.java))


        }
    }


    override fun onResume() {
        super.onResume()

        if (!TextDetectionActivity.absoulatePath.equals("")) {
            val bm = BitmapFactory.decodeFile(TextDetectionActivity.absoulatePath)
            val newBitmap =
                CurvedBottomNavigationView.getRotateImage(TextDetectionActivity.absoulatePath, bm)
            val setedBitmap = CurvedBottomNavigationView.getCenteCropBitmap(newBitmap)
            setBitmapOnImageView(setedBitmap)
            showDialog()
            presenter.onGettingBitmapForImageView(setedBitmap)
            TextDetectionActivity.absoulatePath = ""
        }
    }


}


