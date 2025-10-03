package com.elad.kce.demo.banner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elad.halacha.engine.calendar.BannerLiteRequest
import com.elad.halacha.engine.calendar.BannerLiteResponse
import com.elad.halacha.engine.calendar.BannerLiteService
import com.elad.halacha.engine.calendar.BannerLiteServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

data class BannerUi(
    val occasionHe: String = "—",
    val shaahHms: String = "--:--:--",
    val zmanitMinute: String = "--",
    val candleHHmm: String? = null
)

class BannerViewModel(
    private val service: BannerLiteService = BannerLiteServiceImpl()
) : ViewModel() {

    private val _state = MutableStateFlow(BannerUi())
    val state: StateFlow<BannerUi> = _state

    fun load(
        date: LocalDate,
        lat: Double,
        lon: Double,
        elev: Double?,
        tz: String,
        profileKey: String
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val req = BannerLiteRequest(
                dateIso = date.atStartOfDay(ZoneId.of(tz)).toLocalDate().toString(),
                lat = lat, lon = lon, elev = elev, tz = tz, profileKey = profileKey
            )
            val resp = service.compute(req)
            _state.value = resp.toUi()
        }
    }
}

private fun BannerLiteResponse.toUi(): BannerUi {
    val shaah = Duration.ofSeconds(zmaniyot.shaahSeconds).let { d ->
        "%02d:%02d:%02d".format(d.toHours(), d.toMinutesPart(), d.toSecondsPart())
    }
    val zmanitMin = "${zmaniyot.minuteSeconds} שניות"
    return BannerUi(
        occasionHe = occasion.labelHe,
        shaahHms = shaah,
        zmanitMinute = zmanitMin,
        candleHHmm = candleLightingLocal
    )
}