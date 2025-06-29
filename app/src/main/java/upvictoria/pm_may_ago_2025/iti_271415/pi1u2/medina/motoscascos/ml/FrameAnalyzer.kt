package upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicBoolean

class FrameAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {

    private val yoloDetector = YoloDetector(context)
    private val isAnalyzing = AtomicBoolean(false)

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        if (isAnalyzing.get()) {
            image.close()
            return
        }

        isAnalyzing.set(true)

        val bitmap = imageProxyToBitmap(image)
        if (bitmap != null) {
            val results = yoloDetector.detect(bitmap)

            // Mostrar cada detección individualmente en el log
            for (result in results) {
                Log.d("YoloDetect", result)
            }
        }

        image.close()
        isAnalyzing.set(false)
    }

    // Convierte el frame a Bitmap (formato YUV_420_888 → Bitmap)
    @androidx.camera.core.ExperimentalGetImage
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        if (image.format != ImageFormat.YUV_420_888) return null

        return YuvToRgbConverter(context).yuvToRgb(image)
    }
}