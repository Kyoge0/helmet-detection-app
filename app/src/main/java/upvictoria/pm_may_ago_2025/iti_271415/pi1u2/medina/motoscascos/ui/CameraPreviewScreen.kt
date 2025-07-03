package upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ui

import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.R
import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml.DetectionBox
import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml.DetectionOverlay


import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml.FrameAnalyzer
import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml.YoloDetector

@Composable
fun CameraPreviewScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(ctx), FrameAnalyzer(ctx))
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            analyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "Error binding camera use cases", e)
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DetectionOverlayView(
    modifier: Modifier = Modifier,
    detections: List<DetectionBox>
) {
    AndroidView(
        factory = { context ->
            DetectionOverlay(context).apply {
                setDetections(detections)
            }
        },
        update = {
            it.setDetections(detections)
        },
        modifier = modifier
    )
}

@Composable
fun ImagenPruebaDetector() {
    val context = LocalContext.current

    // Ejecutar solo una vez al componer
    LaunchedEffect(Unit) {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.prueba)
        val detector = YoloDetector(context)
        val resultados = detector.detect(bitmap)

        for (resultado in resultados) {
            Log.d("YoloTest", resultado)
        }
    }
}
