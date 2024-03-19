package com.example.barrier_free

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapView


class TMapActivity : AppCompatActivity() {

    var tmapView: TMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tmap)

        tmapView = TMapView(this)

        val container = findViewById<FrameLayout>(R.id.tmapContainer)
        container.addView(tmapView)

        tmapView?.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)


    }


}