package com.volokhinaleksey.imageconverter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.volokhinaleksey.imageconverter.databinding.ActivityMainBinding
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var filePath = ""

    @RequiresApi(Build.VERSION_CODES.R)
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivity(intent)
        }
    }

    private val presenter by moxyPresenter {
        MainPresenter(MainRepositoryImpl())
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { data ->
                binding.image.setImageURI(data)
                filePath = getPathByUrl(data).toString()
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission.launch(
            arrayOf(
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        binding.takeImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))
            imageLauncher.launch(intent)
        }
        binding.convertImage.setOnClickListener {
            if (filePath.isNotEmpty()) {
                AlertDialog.Builder(this).setTitle("Cancel the operation")
                    .setMessage("You can still cancel the operation")
                    .setPositiveButton("Continue") { dialog, _ ->
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        presenter._model = ImageData(filePath, bitmap)
                        presenter.convertJpgToPng()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
        }
    }

    private fun getPathByUrl(contentUri: Uri): String? {
        var absolutePath: String? = null
        val cursor = contentResolver.query(
            contentUri,
            arrayOf(MediaStore.Images.Media.DATA),
            null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            columnIndex.let {
                absolutePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return absolutePath
    }

    override fun onSuccess(bitmap: Bitmap) {
        binding.image.setImageBitmap(bitmap)
        Toast.makeText(
            this,
            "The image converted from JPG to PNG",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onError(error: Throwable) {
        Toast.makeText(
            this,
            "Error: $error",
            Toast.LENGTH_LONG
        ).show()
        Log.e("", "OnError", error)
    }

}

