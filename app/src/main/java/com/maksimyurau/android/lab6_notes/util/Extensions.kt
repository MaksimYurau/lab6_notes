package com.maksimyurau.android.lab6_notes.util

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromHex(hex: String): Color {
  return Color(android.graphics.Color.parseColor(hex))
}
