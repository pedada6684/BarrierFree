package com.barrier_free

import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

// PlatformViewFactory 클래스를 상속받는 NativeViewFactory 클래스 정의
class NativeViewFactory(activity: FlutterActivity) :
        PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    // FlutterActivity를 저장하는 프로퍼티
    private val activity: FlutterActivity

    // 초기화 블록
    init {
        this.activity = activity
    }

    // PlatformViewFactory의 create 메서드 오버라이드
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        // NativeView 인스턴스 생성하여 반환
        val creationParams = args as Map<String?, Any?>?
        return NativeView(activity, context, viewId, creationParams)
    }
}
