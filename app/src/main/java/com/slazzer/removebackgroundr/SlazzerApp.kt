package com.slazzer.removebackgroundr

import android.app.Application
import com.anthempest.salesapp.constant.Constants
import com.slazzer.bgremover.Slazzer

class SlazzerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Slazzer.init(Constants.API_KEY)

    }
}