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

    private val layout: FrameLayout
    override fun getView(): View {
        return layout
    }


    override fun dispose() {

    }


    init {
        layout = FrameLayout(context!!)

        val tmapView = TMapView(context).apply {
            setSKTMapApiKey(BuildConfig.TMAP_API_KEY) // 수정된 부분

            if (creationParams != null) {
                val longitude = creationParams["longitude"] as Double
                val latitude = creationParams["latitude"] as Double

                Log.d("NativeView", "Longitude: $longitude, Latitude: $latitude")
                setLocationPoint(longitude, latitude)
                setCenterPoint(longitude, latitude)
            }

        }
        layout.addView(tmapView)
    }
}