package com.example.androidnavigatorleti.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.data.UserInfo
import kotlinx.android.synthetic.main.fragment_registration.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment : BaseFragment() {

    companion object {

        const val DATE_FORMAT = "dd.MM.yyyy"
        const val BIRTHDAY_MIN_AGE = 85
        const val BIRTHDAY_MAX_AGE = 18
    }

    private val viewModel: RegistrationVIewModel by viewModels()

    private var errorFlag = false

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    private val minRegistrationDateCalendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR) - BIRTHDAY_MIN_AGE)
        }

    private val maxRegistrationDateCalendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR) - BIRTHDAY_MAX_AGE)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fieldList = listOf(name_text, surname_text, birthday_text)

        approve_button.setOnClickListener {
            for(i in fieldList) { checkIfTextFieldEmptyAndMakeToast(i) }

            if (!errorFlag) {
                viewModel.insertUser(
                    UserInfo(
                        name = name_text.toString(),
                        surname = surname_text.toString(),
                        birthday = birthday_text.toString()
                ))
                openFragment(R.id.history)
            }
        }

        birthday_text.setOnClickListener {
            showRegistrationDatePicker(childFragmentManager, birthday_text)
        }
    }

    private fun checkIfTextFieldEmptyAndMakeToast(view: EditText) {
        if (view.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.input_error, Toast.LENGTH_SHORT).show()
            errorFlag = true
        }
    }

    private fun showRegistrationDatePicker(fragmentManager: FragmentManager, dateField: EditText) {
        DatePickerPopup.show(
                fragmentManager,
                minRegistrationDateCalendar.time,
                maxRegistrationDateCalendar.time,
                dateField.text.toString().let { if (it.isNotEmpty()) dateFormat.parse(it) else null }
        ) { newDate ->
            dateField.setText(dateFormat.format(newDate))
        }
    }
}