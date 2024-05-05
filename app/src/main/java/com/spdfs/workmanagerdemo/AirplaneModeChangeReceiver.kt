package com.spdfs.workmanagerdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/* Broadcast Receiver example
- Broadcast receivers are the events that triggered on the basis of the system conditions like internet availability, battery percentage,
 and when the device restart.
*/
class AirplaneModeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val isAirplaneModeEnabled = intent?.getBooleanExtra("state", false) ?: return

        if (isAirplaneModeEnabled) {
            Toast.makeText(context, "Airplane Mode Enabled", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Airplane Mode Disabled", Toast.LENGTH_LONG).show()
        }
    }
}