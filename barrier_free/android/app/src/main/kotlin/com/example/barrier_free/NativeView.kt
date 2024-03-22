package com.barrier_free

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.BitmapFactory
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.platform.PlatformView
import com.skt.Tmap.TMapView
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.barrier_free.BuildConfig
import android.util.Log

internal class NativeView(
        activity: FlutterActivity,
       private val context: Context?,
        id: Int,
        creationParams: Map<String?, Any?>?
) :
        PlatformView {

    private val layout: FrameLayout = FrameLayout(context!!)
    private val tmapView = TMapView(context).apply {
        setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
    }

    override fun getView(): View = layout

    init {

        if (creationParams != null) {
            val longitude = creationParams["longitude"] as Double
            val latitude = creationParams["latitude"] as Double

            Log.d("NativeView", "Longitude: $longitude, Latitude: $latitude")
//                setLocationPoint(longitude, latitude)
//                setCenterPoint(longitude, latitude)
            updateLocation(longitude, latitude)
        }

        layout.addView(tmapView)
//        NativeViewManager.setNativeView(this)
    }

    //위치 업데이트
    fun updateLocation(longitude: Double, latitude: Double) {

        tmapView.removeMarkerItem("currentLocationMarker")

		tmapView.setLocationPoint(longitude, latitude)
		tmapView.setCenterPoint(longitude, latitude)

		//현재 위치에 마커 추가하기
		val markerItem = TMapMarkerItem().apply {
			tMapPoint = TMapPoint(longitude, latitude)// 마커 위치
			name = "현재 위치"
			visible = TMapMarkerItem.VISIBLE

			//아이콘
//            val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.icon)
//            icon = bitmap

        }
        tmapView.addMarkerItem("currentLocationMarker", markerItem)

    }

    override fun dispose() {
        NativeViewManager.clearNativeView()
    }
}


