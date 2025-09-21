package com.elad.kce.demo

import com.elad.halacha.profiles.api.ProfilesService
import com.elad.halacha.engine.profiles.ProfilesServiceImpl

/** Simple DI for the engine services (in-process, no HTTP). */
object EngineServices {
    val profilesService: ProfilesService by lazy { ProfilesServiceImpl() }
}