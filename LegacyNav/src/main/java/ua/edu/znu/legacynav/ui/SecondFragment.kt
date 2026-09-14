package ua.edu.znu.legacynav.ui

import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ua.edu.znu.legacynav.benchmark.LatencyMeasurement
import ua.edu.znu.legacynav.benchmark.LatencyTracker
import ua.edu.znu.legacynav.R
import ua.edu.znu.legacynav.data.SubjectRepository

/**
 * A simple [Fragment] subclass that represents the second screen in the navigation flow.
 * Retrieves the subject ID passed via Safe Args, looks up the subject in the repository,
 * and displays its data.
 */
class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // T2: Capture when SecondFragment view creation starts
        val t2 = System.nanoTime()
        LatencyTracker.destinationScreenEntered = t2

        val args = SecondFragmentArgs.fromBundle(requireArguments())
        val subject = SubjectRepository.getById(args.subjectId)
        
        // T3: Capture after subject is retrieved
        val t3 = System.nanoTime()
        LatencyTracker.objectRetrieved = t3

        view.findViewById<TextView>(R.id.subjectId).text =
            getString(R.string.subject_id_label, (subject?.id ?: args.subjectId).toString())
        view.findViewById<TextView>(R.id.subjectName).text =
            getString(R.string.subject_name_label, subject?.name ?: "")
        view.findViewById<TextView>(R.id.subjectChecked).text =
            getString(R.string.subject_checked_label, (subject?.isChecked ?: false).toString())
        view.findViewById<TextView>(R.id.categoryName).text =
            getString(R.string.category_name_label, subject?.category?.name ?: "")

        Choreographer.getInstance().postFrameCallback {
            // T4: Capture when first frame is dispatched
            val t4 = System.nanoTime()
            val measurement = LatencyMeasurement(
                navigationInitiated = LatencyTracker.navigationInitiated,
                setupCompleted = LatencyTracker.setupCompleted,
                destinationScreenEntered = LatencyTracker.destinationScreenEntered,
                objectRetrieved = LatencyTracker.objectRetrieved,
                firstFrameDispatched = t4
            )
            Log.d("LatencyMeasurement", measurement.toString())
            LatencyTracker.measurements.add(measurement)
            LatencyTracker.frameLatch.countDown()
        }

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            findNavController().navigate(SecondFragmentDirections.actionSecondToFirst())
        }
    }
}