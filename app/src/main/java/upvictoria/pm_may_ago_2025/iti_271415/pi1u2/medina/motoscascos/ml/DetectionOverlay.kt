package upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

data class DetectionBox(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val label: String,
    val confidence: Float
)

class DetectionOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
    }

    private var boxes: List<DetectionBox> = emptyList()

    fun setDetections(detections: List<DetectionBox>) {
        boxes = detections
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (box in boxes) {
            val left = (box.x - box.w / 2) * width
            val top = (box.y - box.h / 2) * height
            val right = (box.x + box.w / 2) * width
            val bottom = (box.y + box.h / 2) * height

            canvas.drawRect(left, top, right, bottom, paint)
            canvas.drawText("${box.label} ${(box.confidence * 100).toInt()}%", left, top - 10, textPaint)
        }
    }
}
