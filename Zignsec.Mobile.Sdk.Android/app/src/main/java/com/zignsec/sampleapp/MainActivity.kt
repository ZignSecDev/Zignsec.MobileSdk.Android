package com.zignsec.sampleapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.regula.onlineprocessing_without_license_kotlin.databinding.ActivityMainBinding

import com.regula.documentreader.api.enums.*
import com.regula.documentreader.api.results.DocumentReaderResults
import com.zignsec.identification.enums.ZignSecEnvironment
import com.zignsec.identification.activities.ZignSecIdentificationActivity
import com.zignsec.identification.callbacks.ZignSecIdentificationCompletion
import com.zignsec.identification.results.ZignSecIdentificationResults

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.showScannerBtn.setOnClickListener {
            val activity =
                ZignSecIdentificationActivity(
                    ZignSecEnvironment.TEST,
                    "INSERT_SESSION_ID",
                    "INSERT_ACCESS_TOKEN",
                );

            activity.startIdentification(this, completion);
        }
    }

    private val completion =
        ZignSecIdentificationCompletion { results, exception ->
            if (exception != null){
                println(exception.localizedMessage)
            } else if (results != null) {
                println(results.status.name)
            }

        }
}