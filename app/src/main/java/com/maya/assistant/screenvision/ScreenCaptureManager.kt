package com.maya.assistant.screenvision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.ডিসপ্লেManager
import android.hardware.display.Virtualডিসপ্লে
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.Log

class স্ক্রিনCaptureManager(private val context: Context) {
    private val TAG = "SCREEN_CAP"
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualডিসপ্লে: Virtualডিসপ্লে? = null

    fun startCapture(projection: MediaProjection, width: Int, height: Int, dpi: Int) {
        mediaProjection = projection
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualডিসপ্লে = projection.createVirtualডিসপ্লে(
            "MAYA_স্ক্রিন", width, height, dpi,
            ডিসপ্লেManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface, null, null
        )
        Log.d(TAG, "স্ক্রিন capture started")
    }

    fun captureFrame(): Bitmap? {
        val reader = imageReader ?: return null
        val image = reader.acquireLatestImage() ?: return null
        return try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width
            val bmp = Bitmap.createBitmap(
                image.width + rowPadding / pixelStride,
                image.height, Bitmap.Config.ARGB_8888
            )
            bmp.copyPixelsFromBuffer(buffer)
            Bitmap.createBitmap(bmp, 0, 0, image.width, image.height)
        } catch (e: Exception) {
            Log.e(TAG, "Capture error: ${e.message}")
            null
        } finally {
            image.close()
        }
    }

    fun stop() {
        virtualডিসপ্লে?.release()
        imageReader?.close()
        mediaProjection?.stop()
        virtualডিসপ্লে = null; imageReader = null; mediaProjection = null
    }
}
