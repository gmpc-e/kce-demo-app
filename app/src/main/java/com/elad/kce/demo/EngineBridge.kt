package com.elad.kce.demo

import com.elad.halacha.profiles.api.GeoInput
import com.elad.halacha.profiles.api.ProfileComputeInput
import com.elad.halacha.profiles.api.ProfilesService
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime

/**
 * Thin adapter to the new ProfilesService API for the demo app.
 * - Lists profiles (key + display name [Hebrew preferred]).
 * - Computes a selected profile for given date/geo.
 * - Maps response items -> (Hebrew label, HH:mm rounded).
 */
object EngineBridge {

  data class UiProfile(val key: String, val displayName: String)

  private val svc: ProfilesService
    get() = EngineServices.profilesService

  private val ISO_DT: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  /** 1) List profiles for the picker. */
  fun listProfiles(): List<UiProfile> {
    val entries = svc.listProfiles()
    return entries
      .map { e ->
        val display = (e.labels?.he ?: e.displayName).ifBlank { e.key }
        UiProfile(key = e.key, displayName = display)
      }
      .sortedBy { it.displayName }
  }

  /** 2) Compute a selected profile and adapt to UI list. */
  fun computeProfile(
    profileKey: String,
    date: LocalDate,
    lat: Double,
    lon: Double,
    elev: Double,
    tz: String
  ): ComputeResultDemo {

    val input = ProfileComputeInput(
      dateIso = date.toString(),
      geo = GeoInput(lat = lat, lon = lon, elev = elev, tz = tz)
    )

    val res = svc.computeProfile(profileKey, input)

    val zone = ZoneId.of(tz)

    val items: List<ZmanItem> = res.results.mapNotNull { r ->
      val labelHe = r.label?.he ?: r.label?.en ?: r.id

      // Avoid smart-cast: stash local/instant into locals and check
      val localStr: String? = r.local
      val instStr: String? = r.instant

      val localTime: LocalTime? = when {
        !localStr.isNullOrBlank() -> {
          // Strip optional zone suffix like "[Asia/Jerusalem]" if present
          val cleaned = localStr.replace(Regex("\\[.*\\]\$"), "")
          OffsetDateTime.parse(cleaned, ISO_DT).toLocalTime()
        }
        !instStr.isNullOrBlank() -> {
          java.time.Instant.parse(instStr).atZone(zone).toLocalTime()
        }
        else -> null
      }?.roundNoSecondsUp30()

      localTime?.let { ZmanItem(labelHe = labelHe, time = it) }
    }

    val header = res.profile.labels?.he
      ?: res.profile.displayName.ifBlank { res.profile.key }

    return ComputeResultDemo(
      profileName = header,
      locationName = "",            // filled by caller with city name
      date = LocalDate.parse(input.dateIso),
      times = items
    )
  }
}