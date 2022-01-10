package com.example.androidnavigatorleti.presentation.profile

import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.common.base.BaseFragment
import com.example.androidnavigatorleti.common.base.EmptyViewState
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo
import kotlinx.android.synthetic.main.fragment_registration.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment : BaseFragment<RegistrationViewModel, EmptyViewState>(R.layout.fragment_registration) {

    companion object {

        const val DATE_FORMAT = "dd.MM.yyyy"
        const val BIRTHDAY_MIN_AGE = 85
        const val BIRTHDAY_MAX_AGE = 18
    }

    override val viewModel: RegistrationViewModel by viewModels()

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

    override fun renderState(state: EmptyViewState) {
        val fieldList = listOf(name_text, surname_text, birthday_text)

        approve_button.setOnClickListener {
            for(i in fieldList) { checkIfTextFieldEmptyAndMakeToast(i) }

            if (!errorFlag) {
                viewModel.insertUser(
                    DbUserInfo(
                        name = name_text.toString(),
                        surname = surname_text.toString(),
                        birthday = birthday_text.toString()
                    )
                )
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