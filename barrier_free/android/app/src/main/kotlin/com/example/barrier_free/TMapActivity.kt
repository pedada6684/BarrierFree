package com.example.barrier_free

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapView
import com.example.barrier_free.BuildConfig


class TMapActivity : AppCompatActivity() {

    var tmapView: TMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tmap)

        tmapView = TMapView(this).apply {
            setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        }

        val container = findViewById<FrameLayout>(R.id.tmapContainer)
        container.addView(tmapView)

        if (intent.getBooleanExtra("enableTrackingMode", false)) {
            tmapView?.setTrackingMode(true)
        }

    }


}