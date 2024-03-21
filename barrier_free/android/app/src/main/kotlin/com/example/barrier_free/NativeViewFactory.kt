package com.barrier_free

import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory(activity: FlutterActivity) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    private val activity: FlutterActivity

    init {
        this.activity = activity
    }

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return NativeView(activity, context, viewId, creationParams)
    }
}