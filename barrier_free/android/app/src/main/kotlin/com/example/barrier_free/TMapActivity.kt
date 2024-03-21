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
    private lateinit var methodChannel: MethodChannel

//    var tmapView: TMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmap)

        tmapView = TMapView(this).apply {
            setSKTMapApiKey(BuildConfig.TMAP_API_KEY)

        }
        findViewById<FrameLayout>(R.id.tmapContainer).addView(tmapView)

        intent?.let {
            val longitude = it.getDoubleExtra("longitude", 0.0)
            val latitude = it.getDoubleExtra("latitude", 0.0)
            if (latitude != 0.0 && longitude != 0.0) {
                tmapView.setLocationPoint(longitude, latitude)
                tmapView.setCenterPoint(longitude, latitude)
            }
        }
//        val flutterEngine = (application as FlutterEngineProvider).provideFlutterEngine(this)
//
//        if (flutterEngine != null) {
//            methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.barrier_free/tmap")
//            methodChannel.setMethodCallHandler { call, result ->
//                if (call.method == "setCurrentLocation") {
//                    val latitude = call.argument<Double>("latitude") ?: 0.0
//                    val longitude = call.argument<Double>("longitude") ?: 0.0
//                    runOnUiThread {
//                        tmapView.setLocationPoint(longitude, latitude)
//                        tmapView.setCenterPoint(longitude, latitude)
//                    }
//                    result.success("지도의 위치가 업데이트 되었습니다.")
//                } else {
//                    result.notImplemented()
//                }
//            }
//        }
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
}