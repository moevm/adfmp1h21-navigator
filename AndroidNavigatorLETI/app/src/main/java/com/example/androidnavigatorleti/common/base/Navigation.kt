package com.example.androidnavigatorleti.common.base

import androidx.annotation.IdRes
import androidx.navigation.NavDirections

sealed class Navigation {

    class To(val navDirections: NavDirections) : Navigation()

    object Back : Navigation()

    class BackTo(@IdRes val destinationId: Int, val inclusive: Boolean) : Navigation()
}