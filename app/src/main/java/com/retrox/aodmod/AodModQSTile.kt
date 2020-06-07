package com.retrox.aodmod

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.extensions.runAfter

class AodModQSTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateState()
    }

    private fun updateState(){
        val isAvailable = XposedUtils.isEdXposedModuleActive() || XposedUtils.isExpModuleActive(this)
        val isEnabled = AppPref.moduleState
        qsTile.label = getString(R.string.qs_aodmod_title)
        qsTile.icon = if(isEnabled) Icon.createWithResource(this, R.drawable.ic_qs_aod_on) else Icon.createWithResource(this, R.drawable.ic_qs_aod_off)
        qsTile.state = when{
            !isAvailable -> Tile.STATE_UNAVAILABLE
            isEnabled -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateState()
    }

    private val isClickable : Boolean
        get() = qsTile.state != Tile.STATE_UNAVAILABLE

    override fun onClick() {
        super.onClick()
        if(!isClickable) return
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.updateTile()
        AppPref.moduleState = !AppPref.moduleState
        resetPrefPermissions(this)
        runAfter(0.5){
            updateState()
        }
    }

}