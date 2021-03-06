package com.example.android.transportsystem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.budiyev.android.codescanner.*

class ReviewerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewer)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            startScanning()
        }




    }

    private fun startScanning() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        val intent = Intent(this, JourneyCheckedActivity::class.java)
        intent.putExtra("journeyId", "3YLHStlEqlvpkZHZyzKl")
        startActivity(intent)

        /*codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val intent = Intent(this, JourneyCheckedActivity::class.java)
                intent.putExtra("journeyId", it.text)
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }*/
    }

    override fun onResume() {
        super.onResume()
        //codeScanner.startPreview()
    }

    override fun onPause() {
        //codeScanner.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        super.onDestroy()
    }


}