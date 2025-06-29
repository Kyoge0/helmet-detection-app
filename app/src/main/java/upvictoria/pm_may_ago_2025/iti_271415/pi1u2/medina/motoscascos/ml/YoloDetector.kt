package upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class YoloDetector(context: Context) {

    private val interpreter: Interpreter

    init {
        val model = loadModelFile(context, "best_v1_float16.tflite")
        interpreter = Interpreter(model)

        Log.d("YoloDebug", "Input shape: ${interpreter.getInputTensor(0).shape().joinToString()}")
        Log.d("YoloDebug", "Output shape: ${interpreter.getOutputTensor(0).shape().joinToString()}")
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun detect(bitmap: Bitmap): List<String> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

        val outputShape = interpreter.getOutputTensor(0).shape() // [1, 6, 8400]
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, interpreter.getOutputTensor(0).dataType())

        interpreter.run(inputBuffer, outputBuffer.buffer.rewind())
        val output = outputBuffer.floatArray

        val detections = mutableListOf<String>()
        val numPredictions = 8400
        val elementsPerPrediction = 6 // x, y, w, h, confidence, class

        val classNames = listOf("Con casco", "Sin casco")

        for (i in 0 until numPredictions) {
            val x = output[i]
            val y = output[i + 8400]
            val w = output[i + 2 * 8400]
            val h = output[i + 3 * 8400]
            val confidence = output[i + 4 * 8400]
            val classId = output[i + 5 * 8400].toInt()

            if (confidence > 0.5f) {
                val label = classNames.getOrElse(classId) { "Desconocido" }
                detections.add("Clase: $label, Confianza: ${"%.2f".format(confidence)}, BBox [x=$x, y=$y, w=$w, h=$h]")
            }
        }

        Log.d("Yolo", "Detecciones procesadas: ${detections.joinToString()}")
        return detections
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputSize = 640
        val inputChannels = 3
        val byteBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * inputChannels * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixelValue in intValues) {
            val r = (pixelValue shr 16 and 0xFF) / 255.0f
            val g = (pixelValue shr 8 and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f

            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        byteBuffer.rewind()
        return byteBuffer
    }
}
