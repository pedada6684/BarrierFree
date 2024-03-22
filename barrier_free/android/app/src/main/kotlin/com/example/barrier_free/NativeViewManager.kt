package com.barrier_free

internal object NativeViewManager {
    private var nativeView: NativeView? = null

    internal fun setNativeView(view: NativeView) {
        nativeView = view
    }

    internal fun getNativeView(): NativeView? = nativeView

    internal fun clearNativeView() {
        nativeView = null
    }

    internal fun updateLocation(longitude: Double, latitude: Double) {
        nativeView?.updateLocation(longitude, latitude)
    }
}