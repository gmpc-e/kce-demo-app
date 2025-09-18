package com.elad.kce.demo

import kotlinx.serialization.Serializable
import java.time.*

@Serializable
data class City(
  val name: String,
  val lat: Double,
  val lon: Double,
  val elev: Double = 0.0,
  val tz: String = "Asia/Jerusalem"
)

data class ComputeRequestDemo(
  val date: LocalDate,
  val city: City,
  val profileExternalName: String
)

data class ZmanItem(
  val labelHe: String,
  val time: LocalTime // rounded HH:mm (no seconds)
)

data class ComputeResultDemo(
  val profileName: String,
  val locationName: String,
  val date: LocalDate,
  val times: List<ZmanItem>
)

/** Round: if seconds > 30 -> minute up; strip seconds & nanos. */
fun LocalTime.roundNoSecondsUp30(): LocalTime {
  val plus = if (this.second > 30) 1 else 0
  return this.withSecond(0).withNano(0).plusMinutes(plus.toLong())
}
