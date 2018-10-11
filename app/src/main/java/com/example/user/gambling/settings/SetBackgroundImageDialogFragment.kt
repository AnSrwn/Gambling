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
import com.example.user.gambling.MainActivity.Companion.BACKGROUND_IMAGE_URI_KEY
import com.example.user.gambling.MainActivity.Companion.DEFAULT_DRAWABLE_URI
import com.example.user.gambling.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

/**
 * DialogFragment for setting background image. Choosing either from camera, gallery or reset to default.
 */
class SetBackgroundImageDialogFragment : DialogFragment() {

    companion object {
        internal const val REQUEST_TAKE_PHOTO = 1
        internal const val REQUEST_PICK_PHOTO = 2

        /** Variable for the background pictures name. */
        private const val BACKGROUND_PICTURE = "background_pic"
    }

    /** For the intents photo result. */
    private var currentPhotoURI : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.dialog_background_image, container, false)
        val btnTakePhoto: Button = view!!.findViewById(R.id.takePhotoDialog)
        val btnImportFromGallery: Button = view.findViewById(R.id.importFromGalleryDialog)
        val btnReset: Button = view.findViewById(R.id.setToDefault)

        btnTakePhoto.setOnClickListener {
            if (!isCameraAvailable()) {
                throw IllegalArgumentException("No Camera")
            }
            dispatchTakePictureIntent()
            //dialog.dismiss() -> Does not work TODO FIX
        }

        btnImportFromGallery.setOnClickListener {
            dispatchGalleryIntent()
            //dialog.dismiss() -> Does not work TODO FIX
        }

        btnReset.setOnClickListener {
            currentPhotoURI = DEFAULT_DRAWABLE_URI //Quick and dirty
            addBackgroundImagePathToPreferene(currentPhotoURI)
            //dialog.dismiss() -> Does not work TODO FIX
        }

        return view
    }

    private fun isCameraAvailable(): Boolean {
        val pm = activity!!.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /**
     * Starting taking picture intent and saving the captured image in a temporary file.
     */
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
                            "com.example.user.gambling.fileprovider",
                            file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    /**
     * Create or replace the background picture in app storage in the directory specified in xml/file_paths.xml
     * @return File object at currentPhotoURI
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${BACKGROUND_PICTURE}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoURI = absolutePath
        }
    }

    /**
     * Save selected image path in shared preferences. If data is null throw Exception.
     * @param data selected image. If null throws NullpointerException.
     */
    private fun handleGalleryIntent(data: Intent?) {
        if (data == null) {
            // something is wrong
            Log.d("DBG", "No Picture selected")
            throw NullPointerException("Data is null!")
        }
        // handle single photo
        val uri = data.data
        importPhoto(activity!!.applicationContext, uri)
        addBackgroundImagePathToPreferene(currentPhotoURI)
    }

    /**
     * Handle camera intent after picture was taken and saved to applications local storage.
     */
    private fun handleCameraIntent() {
        addBackgroundImagePathToPreferene(currentPhotoURI)
    }

    /**
     * Add background image uri in the local application storage to shared preference with key imageURL.
     * @param uri of the background image in the local storage.
     */
    private fun addBackgroundImagePathToPreferene(uri : String){
        val sp : SharedPreferences.Editor  = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext).edit()
        sp.putString(BACKGROUND_IMAGE_URI_KEY, uri)
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

    /**
     * Handle gallery intent for selecting only image-Files
     */
    private fun dispatchGalleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_PHOTO)
    }

    /**
     * Import image from gallery at the given uri into the applications context.
     */
    @Throws(IOException::class)
    private fun importPhoto(context: Context, uri: Uri): Boolean {
        if (!isImage(context, uri)) {
            // not image
            return false
        }

        return try {
            val photoFile = createImageFile()
            copyUriToFile(context, uri, photoFile)
            // addImageToGallery(photoFile) -> application gallery
            true
        } catch (e: IOException) {
            e.printStackTrace()
            // handle error
            false
        }
    }

    /**
     * Checks whether a file at the fiven uri is an image.
     * @param context of the activity
     * @param uri of the image
     */
    private fun isImage(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri) ?: return true
        return mimeType.startsWith("image/")
    }

    /**
     * Copy selected image at uri to the applications local storage.
     * @param context of the activity
     * @param uri of the image
     * @param outputFile to write the image file from the uri
     */
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