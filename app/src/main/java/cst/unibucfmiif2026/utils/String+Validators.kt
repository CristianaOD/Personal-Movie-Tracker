package cst.unibucfmiif2026.utils

import android.util.Patterns

fun String.isValidEmail() = isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() = this.length >= 6

fun String.isValidName() = this.length >= 3