package com.example.androidnavigatorleti

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.androidnavigatorleti.base.Navigation

const val PERMISSION_REQUEST_CODE = 1001
const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002

fun Fragment.checkLocationAndForegroundServicePermissions(): Boolean =
    requireContext().checkLocationAndForegroundServicePermissions()

fun Context.checkLocationAndForegroundServicePermissions(): Boolean =
    checkLocationPermission() && checkForegroundServicePermission()

fun Context.checkLocationPermission(): Boolean =
    checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION)

fun Context.checkForegroundServicePermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        checkSinglePermission(Manifest.permission.FOREGROUND_SERVICE)
    } else {
        true
    }

fun Fragment.requestLocationPermissions() {
    val permissions: MutableList<String> = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (activity != null && isAdded) {
        requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }
}

private fun Context.checkSinglePermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * For use with `when` expression.
 * @see <a href="https://proandroiddev.com/til-when-is-when-exhaustive-31d69f630a8b">https://proandroiddev.com/til-when-is-when-exhaustive-31d69f630a8b</a>
 */
val <T> T.exhaustive: T
    get() = this

fun NavController.navigateTo(navigation: Navigation) {
    @Suppress("IMPLICIT_CAST_TO_ANY")
    when (navigation) {
        is Navigation.To -> navigate(navigation.navDirections)
        is Navigation.Back -> popBackStack()
        is Navigation.BackTo -> popBackStack(navigation.destinationId, navigation.inclusive)
    }.exhaustive
}