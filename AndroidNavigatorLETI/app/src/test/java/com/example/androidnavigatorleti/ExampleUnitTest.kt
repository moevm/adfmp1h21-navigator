package com.example.androidnavigatorleti

import com.example.androidnavigatorleti.data.UserInfo
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.ui.profile.RegistrationFragment
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat(RegistrationFragment.DATE_FORMAT, Locale.getDefault())

    private val minRegistrationDateCalendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR) - RegistrationFragment.BIRTHDAY_MIN_AGE)
        }

    private val maxRegistrationDateCalendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR) - RegistrationFragment.BIRTHDAY_MAX_AGE)
        }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `check user info full empty or filled`() {
        with(USER_INFO) {
            assert(
                name.isNotEmpty() && surname.isNotEmpty() && birthday.isNotEmpty()
                        || name.isEmpty() && surname.isEmpty() && birthday.isEmpty()
            )
        }
    }

    @Test
    fun `check birthday is valid`() {
        assert(
            dateFormat.parse(VALID_BIRTHDAY).before(maxRegistrationDateCalendar.time)
                    && dateFormat.parse(VALID_BIRTHDAY).after(minRegistrationDateCalendar.time)
        )
    }

    @Test
    fun `check birthday is invalid`() {
        assert(
            !(dateFormat.parse(INVALID_BITHDAY).after(minRegistrationDateCalendar.time)
                    && dateFormat.parse(INVALID_BITHDAY).before(maxRegistrationDateCalendar.time))
        )
    }

    @Test
    fun `check user location is default`() {
        with(USER_LOCATION) {
            assert(lat == 0.0 && lng == 0.0)
        }
    }

    private companion object {
        const val VALID_BIRTHDAY = "01.01.1991"
        const val INVALID_BITHDAY = "01.01.1901"
        val USER_INFO = UserInfo(name = "name", surname = "surname", birthday = "01.11.1999")
        val USER_LOCATION = UserLocation(lat = 0.0, lng = 0.0)
    }
}