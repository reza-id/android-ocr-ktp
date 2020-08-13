package dev.juara.ocrktp

import android.app.Application
import dev.juara.ocrktp.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class OcrApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@OcrApplication)
            androidFileProperties()
            modules(koinModules)
        }
    }
}