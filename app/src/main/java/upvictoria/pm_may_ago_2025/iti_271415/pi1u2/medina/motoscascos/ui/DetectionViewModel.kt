package upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import upvictoria.pm_may_ago_2025.iti_271415.pi1u2.medina.motoscascos.ml.DetectionBox

class DetectionViewModel : ViewModel() {
    val detections = mutableStateListOf<DetectionBox>()

    fun updateDetections(newBoxes: List<DetectionBox>) {
        detections.clear()
        detections.addAll(newBoxes)
    }
}