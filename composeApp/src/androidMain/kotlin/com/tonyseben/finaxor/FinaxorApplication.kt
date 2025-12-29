package com.tonyseben.finaxor

import android.app.Application
import com.tonyseben.finaxor.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class FinaxorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FinaxorApplication)
        }
    }
}
