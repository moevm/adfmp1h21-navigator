package com.example.androidnavigatorleti.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.androidnavigatorleti.R
import kotlinx.android.synthetic.main.popup_date_picker.*
import java.util.*

class DatePickerPopup(
        minDate: Date,
        maxDate: Date,
        private val initialDate: Date?,
        private val onDateSetCallback: (Date) -> Unit
) : DialogFragment() {

    private val calendar: Calendar = Calendar.getInstance()

    private val minYear = getFieldFromStringDate(minDate, Calendar.YEAR)
    private val minMonth = getFieldFromStringDate(minDate, Calendar.MONTH) + 1
    private val minDay = getFieldFromStringDate(minDate, Calendar.DAY_OF_MONTH)

    private val maxYear = getFieldFromStringDate(maxDate, Calendar.YEAR)
    private val maxMonth = getFieldFromStringDate(maxDate, Calendar.MONTH) + 1
    private val maxDay = getFieldFromStringDate(maxDate, Calendar.DAY_OF_MONTH)

    private var chosenYear: Int? = null
    private var chosenMonth: Int? = null
    private var chosenDay: Int? = null

    private var monthOffset = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Для нормального закругления углов диалога
        dialog?.window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return inflater.inflate(R.layout.popup_date_picker, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialDate?.let {
            chosenYear = getFieldFromStringDate(it, Calendar.YEAR)
            chosenMonth = getFieldFromStringDate(it, Calendar.MONTH) + 1
            chosenDay = getFieldFromStringDate(it, Calendar.DAY_OF_MONTH)
        }

        initYearPicker()
        initMonthPicker()
        picker_day.value = chosenDay ?: maxDay

        ok.setOnClickListener {
            calendar.set(picker_year.value, picker_month.value + monthOffset - 1, picker_day.value)

            onDateSetCallback.invoke(calendar.time)

            dialog?.dismiss()
        }

        cancel.setOnClickListener { dialog?.dismiss() }
    }

    private fun initYearPicker() {
        val yearsCapacity = maxYear - minYear + 1
        val yearsArray = Array(yearsCapacity) { i -> (minYear + i).toString() }

        with(picker_year) {
            this.setPickerRange(minYear, maxYear)
            displayedValues = yearsArray
            value = chosenYear ?: maxYear
            initCalendarFromYear(value)

            setOnValueChangedListener { _, _, newVal ->
                initCalendarFromYear(newVal)
            }
        }
    }

    private fun initMonthPicker() {
        with(picker_month) {
            value = chosenMonth?.let { it - monthOffset } ?: maxMonth
            initCalendarFromMonth(value)
            setOnValueChangedListener { _, _, newVal ->
                initCalendarFromMonth(newVal)
            }
        }
    }

    private fun initCalendarFromYear(year: Int) {
        with(picker_month) {
            val realValue = value + monthOffset
            displayedValues = getDisplayedValuesArray()
            this.setPickerRange(1, 12)
            value = realValue
            picker_day.setPickerRange(1, getDayPickerMaxValue(value, year))

            if (maxYear == minYear) {
                this.setPickerRange(1, maxMonth - minMonth + 1)
                displayedValues = getDisplayedValuesArray(minMonth - 1, maxMonth)
                this.setPickerRange(1, displayedValues.size)
                value = if (value <= maxValue) value else maxValue
            } else {
                when (year) {
                    maxYear -> {
                        maxValue = maxMonth
                        value = if (value <= maxValue) value else maxValue
                    }
                    minYear -> {
                        maxValue = maxValue - minMonth + 1
                        displayedValues = getDisplayedValuesArray(minMonth - 1, 12)
                        this.setPickerRange(1, displayedValues.size)
                        value = if (realValue - monthOffset > minValue) realValue - monthOffset else minValue
                    }
                }
            }

            initCalendarFromMonth(value)
        }
    }

    private fun initCalendarFromMonth(month: Int) {
        picker_day.setPickerRange(1, getDayPickerMaxValue(month + monthOffset, picker_year.value))

        if (maxYear == minYear) {
            if (maxMonth == minMonth) {
                picker_day.setPickerRange(minDay, maxDay)
            } else {
                when (month) {
                    picker_month.minValue -> {
                        picker_day.setPickerRange(
                                minDay,
                                getMaxNumberOfDays(picker_month.value + monthOffset, picker_year.value)
                        )
                    }
                    picker_month.maxValue -> {
                        picker_day.setPickerRange(1, maxDay)
                    }
                }
            }
        } else {
            when (picker_year.value) {
                maxYear -> {
                    picker_day.maxValue = if (month == maxMonth) {
                        maxDay
                    } else {
                        getDayPickerMaxValue(month, picker_year.value)
                    }
                }
                minYear -> {
                    picker_day.minValue = if (month == minMonth - monthOffset) {
                        minDay
                    } else {
                        1
                    }
                }
            }
        }
    }

    private fun getDayPickerMaxValue(month: Int, year: Int): Int {
        return if (picker_year.value == maxYear && picker_month.value == maxMonth) {
            maxDay
        } else {
            getMaxNumberOfDays(month, year)
        }
    }

    private fun getMaxNumberOfDays(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31

            4, 6, 9, 11 -> 30

            else -> if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) 29 else 28
        }
    }

    private fun getDisplayedValuesArray(start: Int? = null, end: Int? = null): Array<String> {
        return if (start != null && end != null) {
            monthOffset = start
            resources.getStringArray(R.array.months_array).toList().subList(start, end).toTypedArray()
        } else {
            monthOffset = 0
            resources.getStringArray(R.array.months_array)
        }
    }

    private fun getFieldFromStringDate(date: Date, field: Int): Int = calendar.also { it.time = date }.get(field)

    private fun NumberPicker.setPickerRange(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue
    }

    companion object {

        fun show(
                fragmentManager: FragmentManager,
                minDate: Date,
                maxDate: Date,
                initialDate: Date?,
                onDateSetCallback: (Date) -> Unit
        ) = DatePickerPopup(
                minDate,
                maxDate,
                initialDate,
                onDateSetCallback
        ).show(fragmentManager, DatePickerPopup::class.java.simpleName)
    }
}