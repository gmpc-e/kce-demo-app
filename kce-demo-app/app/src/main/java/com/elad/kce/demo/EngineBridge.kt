package com.elad.kce.demo

import android.content.Context
import java.time.ZoneId

/**
 * Bridge to the KosherJava Compute Engine.
 * - Fetches profiles directly from the engine (no client-side lists).
 * - Computes by profile external name; engine owns/validates profiles.
 *
 * Adjust the package names/types if your engine namespaces differ.
 */
object EngineBridge {

  /** Engine profile item -> demo profile choice. */
  data class EngineProfile(
    val externalName: String, // e.g., "sfarad"
    val labelHe: String       // Hebrew display name
  )

  /** Ask the engine for available profiles. */
  fun getProfiles(context: Context): List<EngineProfile> {
    val engineProfiles = com.elad.halacha.engine.compute.ZmanimComputer.listProfiles()
    return engineProfiles.map { p ->
      // If your engine uses a different model, tweak here.
      val labelHe = p.label.he
      EngineProfile(externalName = p.externalName, labelHe = labelHe)
    }
  }

  /** Compute by engine profile external name. */
  fun compute(context: Context, req: ComputeRequestDemo): ComputeResultDemo {
    val tz = ZoneId.of(req.city.tz)

    val engineReq = com.elad.halacha.engine.model.ComputeRequest(
      date = req.date,
      latitude = req.city.lat,
      longitude = req.city.lon,
      elevation = req.city.elev,
      timeZone = req.city.tz
    )

    val engineRes = com.elad.halacha.engine.compute.ZmanimComputer
      .computeByExternalName(req.profileExternalName, engineReq)

    val items = engineRes.items.map { it ->
      val labelHe = it.label.he
      val localTime = it.instant.atZone(tz).toLocalTime().roundNoSecondsUp30()
      ZmanItem(labelHe = labelHe, time = localTime)
    }

    return ComputeResultDemo(
      profileName = engineRes.profileName,
      locationName = req.city.name,
      date = req.date,
      times = items
    )
  }
}
