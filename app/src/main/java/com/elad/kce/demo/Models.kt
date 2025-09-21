package com.elad.kce.demo

import java.time.LocalDate
import java.time.LocalTime

// --- UI-facing models (demo layer only) ---

data class City(
  val name: String,
  val lat: Double,
  val lon: Double,
  val elev: Double,
  val tz: String
)

data class ZmanItem(
  val labelHe: String,
  val time: LocalTime
)

data class ComputeResultDemo(
  val profileName: String,
  val locationName: String,
  val date: LocalDate,
  val times: List<ZmanItem>
)

// Presentation rounding helper: define ONCE here
fun LocalTime.roundNoSecondsUp30(): LocalTime {
  val plus = if (this.second > 30) 1 else 0
  return this.withSecond(0).withNano(0).plusMinutes(plus.toLong())
}