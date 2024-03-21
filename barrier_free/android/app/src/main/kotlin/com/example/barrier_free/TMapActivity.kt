package com.barrier_free

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import com.barrier_free.BuildConfig
import com.skt.Tmap.TMapView
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterEngineProvider


class TMapActivity : AppCompatActivity() {
    //flutter activity에서만 methodchannel 이용가능함
    private lateinit var tmapView: TMapView

    companion object {
        @JvmStatic
        var currentInstance: TMapActivity? = null
    }

    private lateinit var methodChannel: MethodChannel

//    var tmapView: TMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmap)

        currentInstance = this

        tmapView = TMapView(this).apply {
            setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        }
        findViewById<FrameLayout>(R.id.tmapContainer).addView(tmapView)

        intent?.extras?.let {
            updateLocation(it.getDouble("latitude", 0.0), it.getDouble("longitude", 0.0))
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        // 위치 업데이트 로직
        if (latitude != 0.0 && longitude != 0.0) {
            tmapView.setLocationPoint(longitude, latitude)
            tmapView.setCenterPoint(longitude, latitude)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentInstance = null // 인스턴스 참조 제거
    }
}


//    private fun setCurrentLocation(latitude: Double, longitude: Double) {
//        val markerItem = TMapMarkerItem().apply {
//            tMapPoint = TMapPoint(longitude, latitude)
//            name = "현재 위치"
//            visible = TMapMarkerItem.VISIBLE
//
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon)
//            icon = bitmap
//
//            setPosition(0.5f, 1.0f)
//
//        }

//        tmapView.addMarkerItem("currentLocationMarker", markerItem)
//        tmapView.setLocationPoint(longitude, latitude)
//        tmapView.setCenterPoint(longitude, latitude)//화면 중심 마커로 이동
//        tmapView.setTrackingMode(true)
