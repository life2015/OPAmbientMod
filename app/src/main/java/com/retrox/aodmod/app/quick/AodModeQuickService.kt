package com.retrox.aodmod.app.quick

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.os.Bundle
import android.app.DialogFragment;
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext


class AodModeQuickService : TileService() {

    private var isTileActive: Boolean = false

    override fun onClick() {

        // Get the tile's current state.
        val tile = qsTile
        isTileActive = tile.state == Tile.STATE_ACTIVE

        val dialogBuilder = QSDialog.Builder(applicationContext)

        val dialog = dialogBuilder
            .setClickListener(object : QSDialog.QSDialogListener {
                override fun onDialogPositiveClick(dialog: DialogFragment) {
                    Log.d("QS", "Positive registed")

                    // The user wants to change the tile state.
                    isTileActive = !isTileActive
                    updateTile()
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    Log.d("QS", "NegaQSPaneltive registered")

                    // The user is cancelled the dialog box.
                    // We can't do anything to the dialog box here,
                    // but we can do any cleanup work.
                }
            })
            .create()

        // Pass the tile's current state to the dialog.
        val args = Bundle()
        args.putBoolean(QSDialog.TILE_STATE_KEY, isTileActive)

        this.showDialog(dialog.onCreateDialog(args))
    }

    private fun updateTile() {
        val tile = super.getQsTile()
        val activeState = if (isTileActive)
            Tile.STATE_ACTIVE
        else
            Tile.STATE_INACTIVE

        tile.state = activeState
        tile.updateTile()
    }
}
