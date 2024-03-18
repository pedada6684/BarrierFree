package com.example.barrier_free

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.platform.PlatformView

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
        layout.setBackgroundColor(Color.argb(255, 230, 230, 230))

        val textview = TextView(context!!)
        textview.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textview.setText("이 화면은 안드로이드 화면입니다.\nfloatingActionButton 클릭 시 티맵화면이 띄어집니다.")
        textview.gravity = Gravity.CENTER
        layout.addView(textview)
    }
}