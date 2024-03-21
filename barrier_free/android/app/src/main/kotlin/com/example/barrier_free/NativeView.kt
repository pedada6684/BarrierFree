package com.barrier_free

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.platform.PlatformView
import com.skt.Tmap.TMapView
import com.barrier_free.BuildConfig
import android.util.Log

internal class NativeView(
        activity: FlutterActivity,
        context: Context?,
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

    fun updateLocation(longitude: Double, latitude: Double) {

        tmapView.setLocationPoint(longitude, latitude)
        tmapView.setCenterPoint(longitude, latitude)
    }

    override fun dispose() {
//        NativeViewManager.clearNativeView()
    }
}


