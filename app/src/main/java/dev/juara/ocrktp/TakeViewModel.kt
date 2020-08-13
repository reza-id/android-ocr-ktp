package dev.juara.ocrktp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.vision.text.FirebaseVisionText
import dev.juara.ocrktp.data.KpuApi
import dev.juara.ocrktp.di.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import java.util.regex.Pattern

class TakeViewModel(private val kpuApi: KpuApi) : ViewModel() {

    var lines = mutableListOf<String>()
    var nik = "error"
    var gender = "error"
    var name = "error"
    var birthDate = "error"
    var birthPlace = "error"
    var address = "error"

    private val _gotResult = MutableLiveData<Event<Boolean>>()
    val gotResult: LiveData<Event<Boolean>> = _gotResult

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    fun takePicture() {
        _loading.value = true
    }

    fun processTextResult(result: FirebaseVisionText) {
        Timber.d(result.text)
        for (tb in result.textBlocks)
            for (tl in tb.lines)
                lines.add(tl.text)
        getNik()
    }

    private fun getNik() {
        val regexKtpPattern = "[0-9]{16}"
        val pattern = Pattern.compile(regexKtpPattern)

        var found = false
        for (line in lines) {
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                nik = matcher.group()
                var tgl = nik.substring(6, 8)
                var tglInt = tgl.toInt()
                if (tglInt > 40) {
                    gender = "PEREMPUAN"
                    tglInt -= 40
                    tgl = if (tglInt < 10) "0$tglInt" else tglInt.toString()
                } else {
                    gender = "LAKI-LAKI"
                }
                birthDate = "$tgl-${nik.substring(8, 10)}-19${nik.substring(10, 12)}"
                found = true
            }
        }
        if (found) {
            val rawBirth = lines.find { it.contains(birthDate) }
            Timber.d("$rawBirth")
            rawBirth?.let {
                val first = it.replace(birthDate, "").replace(",", "").trim()
                if (first.toLowerCase(Locale.getDefault()).contains("lahir")) {
                    val lowercase = first.toLowerCase(Locale.getDefault())
                    val split = lowercase.split("lahir")
                    birthPlace = split[1].replace(":", "").toUpperCase(Locale.getDefault()).trim()
                } else if (first.contains(":")) {
                    birthPlace = if (!first.startsWith(":")) {
                        val split = first.split(":")
                        split[1].trim()
                    } else first.replace(":", "").trim()
                } else birthPlace = first.trim()
            }
        }

        viewModelScope.launch {
            if (found) getName()
            _loading.value = false
            _gotResult.value = Event(found)
        }

        Timber.d("$nik\n$gender\n$birthPlace, $birthDate")
    }

    private suspend fun getName() = withContext(Dispatchers.IO) {
        try {
            val result = kpuApi.getDps(nik)
            if (result.aaData.isNotEmpty()) {
                name = result.aaData[0].nama
            }
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }
}