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

    internal fun updateLocation(latitude: Double, longitude: Double) {
        nativeView?.updateLocation(longitude, latitude)
    }
}