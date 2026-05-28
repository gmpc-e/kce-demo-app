package com.elad.kce.demo

import java.time.LocalTime

data class City(
  val name: String,
  val lat: Double,
  val lon: Double,
  val elev: Double? = null,
  val candleMinutes: Int = 30,
  val tz: String = "Asia/Jerusalem"
)

data class UiProfile(
  val key: String,
  val displayName: String
)

data class ZmanItem(
  val labelHe: String,
  val time: LocalTime,
  val bold: Boolean = false
)