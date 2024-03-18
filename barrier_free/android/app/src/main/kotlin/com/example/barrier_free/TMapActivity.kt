package com.example.barrier_free

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapView


class TMapActivity : AppCompatActivity() {

    val API_KEY = "JChx6o3smL7JUiVxcOIBY56ihstjNuFv4e2xJBDi" // 발급받은 API 키

    var tmapView: TMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tmap)

        tmapView = TMapView(this)

        val container = findViewById<FrameLayout>(R.id.tmapContainer)
        container.addView(tmapView)

        tmapView?.setSKTMapApiKey(API_KEY)


    }


}