package com.example.user.gambling.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.user.gambling.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class SetBackgroundImageDialogFragment : DialogFragment() {

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_PICK_PHOTO = 1
    }

    private var currentPhotoPath: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.dialog_background_image, container, false)
        val btnTakePhoto: Button = view!!.findViewById(R.id.takePhotoDialog)
        val btnImportFromGallery: Button = view.findViewById(R.id.importFromGalleryDialog)

        btnTakePhoto.setOnClickListener {
            if (!isCameraAvailable()) {
                throw IllegalArgumentException("No Camera")
            }
            dispatchTakePictureIntent()
        }

        btnImportFromGallery.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun isCameraAvailable(): Boolean {
        val pm = activity!!.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also { file ->
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity!!.applicationContext,
                            "com.exampleuser.gambling.camera",
                            file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun handleGalleryIntent(data: Intent?) {
        if (data == null) {
            // something is wrong
            Log.d("DBG", "No Picture selected")
            throw NullPointerException("Data is null!")
        }
        // handle single photo
        val uri = data.data
        importPhoto(activity!!.applicationContext, uri)
        addImagePathToPreferene(currentPhotoPath)
    }

    private fun handleCameraIntent() {
        addImagePathToPreferene(currentPhotoPath)
    }

    private fun addImagePathToPreferene(url : String){
        val sp : SharedPreferences.Editor  = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext).edit()
        sp.putString("imageURL", url)
        sp.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            handleCameraIntent()
        }

        if (requestCode == REQUEST_PICK_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            handleGalleryIntent(data)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_PHOTO)
    }

    private fun importPhoto(context: Context, uri: Uri): Boolean {
        if (!isImage(context, uri)) {
            // not image
            return false
        }

        return try {
            val photoFile = createImageFile()
            copyUriToFile(context, uri, photoFile)
            // addImageToGallery(photoFile)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            // handle error
            false
        }
    }

    private fun isImage(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri) ?: return true
        return mimeType.startsWith("image/")
    }

    private fun copyUriToFile(context: Context, uri: Uri, outputFile: File) {
        val inputStream = context.contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(outputFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
    }


}