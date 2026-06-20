package com.maya.assistant.service

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log

object GestureExecutor {

    private const val TAG = "MAYA_GESTURE"

    fun tap(
        service: অ্যাক্সেসিবিলিটিService,
        x: Int,
        y: Int
    ): Boolean {

        return try {

            val path = Path().apply {
                moveTo(
                    x.toFloat(),
                    y.toFloat()
                )
            }

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            120
                        )
                    )
                    .build()

            val success = service.dispatchGesture(
                gesture,
                null,
                null
            )

            Log.d(TAG, "Tap -> ($x,$y) : $success")

            success

        } catch (e: Exception) {
            Log.e(TAG, "Gesture failed: ${e.message}")
            false
        }
    }

    fun longTap(
        service: অ্যাক্সেসিবিলিটিService,
        x: Int,
        y: Int
    ): Boolean {

        return try {

            val path = Path().apply {
                moveTo(
                    x.toFloat(),
                    y.toFloat()
                )
            }

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            800
                        )
                    )
                    .build()

            service.dispatchGesture(
                gesture,
                null,
                null
            )

        } catch (e: Exception) {
            false
        }
    }

    fun swipe(
        service: অ্যাক্সেসিবিলিটিService,
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int
    ): Boolean {

        return try {

            val path = Path().apply {
                moveTo(
                    startX.toFloat(),
                    startY.toFloat()
                )

                lineTo(
                    endX.toFloat(),
                    endY.toFloat()
                )
            }

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            300
                        )
                    )
                    .build()

            service.dispatchGesture(
                gesture,
                null,
                null
            )

        } catch (e: Exception) {
            false
        }
    }
}