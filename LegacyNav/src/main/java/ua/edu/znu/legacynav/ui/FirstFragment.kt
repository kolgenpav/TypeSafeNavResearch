package ua.edu.znu.legacynav.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import ua.edu.znu.legacynav.benchmark.LatencyTracker
import ua.edu.znu.legacynav.R
import ua.edu.znu.legacynav.data.Category
import ua.edu.znu.legacynav.data.Subject
import ua.edu.znu.legacynav.data.SubjectRepository

/**
 * A simple [Fragment] subclass that represents the first screen in the navigation flow.
 * Reads subject data from inputs, saves it to the repository, and navigates to the second
 * fragment passing the saved subject's ID via Safe Args.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtSubjectName = view.findViewById<EditText>(R.id.txtSubjectName)
        val isSubjectChecked = view.findViewById<CheckBox>(R.id.isSubjectChecked)
        val txtCategoryName = view.findViewById<EditText>(R.id.txtCategoryName)
        val btnNavigate = view.findViewById<Button>(R.id.btnNavigate)

        btnNavigate.setOnClickListener {
            val category = Category(id = 1, name = txtCategoryName.text.toString())
            val subject = Subject(
                id = 0, // assigned by repository
                name = txtSubjectName.text.toString(),
                isChecked = isSubjectChecked.isChecked,
                category = category
            )
            LatencyTracker.navigationStartNs = System.nanoTime()
            val saved = SubjectRepository.add(subject)
            val action = FirstFragmentDirections.actionFirstToSecond(saved.id)
            findNavController().navigate(action)
            LatencyTracker.handoffSetupEndNs = System.nanoTime()
        }
    }
}