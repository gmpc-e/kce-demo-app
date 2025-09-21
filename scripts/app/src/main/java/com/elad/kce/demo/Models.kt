package com.elad.kce.demo

import java.time.LocalTime

data class City(
  val name: String,
  val lat: Double,
  val lon: Double,
  val elev: Double? = null
)

data class UiProfile(
  val key: String,
  val displayName: String
)

data class ZmanItem(
  val labelHe: String,
  val time: LocalTime
)