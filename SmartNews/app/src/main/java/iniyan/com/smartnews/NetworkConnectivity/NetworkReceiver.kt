package iniyan.com.smartnews.NetworkConnectivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo



open class NetworkReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {


        getConnectivityStatusString(context)

    }




    companion object {
        var TYPE_WIFI = 1
        var TYPE_MOBILE = 2
        var TYPE_NOT_CONNECTED = 0

        fun getConnectivityStatus(context: Context): Int {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            if (null != activeNetwork) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI

                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }

        fun getConnectivityStatusString(context: Context): String? {
            val conn = getConnectivityStatus(context)
            var status: String? = null
            if (conn == TYPE_WIFI) {
                status = "Wifi enabled"

            } else if (conn == TYPE_MOBILE) {
                status = "Mobile data enabled"
            } else if (conn == TYPE_NOT_CONNECTED) {
                status = "Not connected to Internet"
            }




                return status
        }


    }


}


//
//
//override fun onResume() {
//    super.onResume()
//    registerInternetCheckReceiver()
//}
//
//override fun onPause() {
//    super.onPause()
//    unregisterReceiver(broadcastReceiver)
//}
//
///**
// * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
// */
//private fun registerInternetCheckReceiver() {
//    val internetFilter = IntentFilter()
//    internetFilter.addAction("android.net.wifi.STATE_CHANGE")
//    internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
//    registerReceiver(broadcastReceiver, internetFilter)
//}
//
//private fun setSnackbarMessage(status: String, showBar: Boolean) {
//    var internetStatus = ""
//    if (status.equals("Wifi enabled", ignoreCase = true) || status.equals("Mobile data enabled", ignoreCase = true)) {
//        internetStatus = "Internet Connected"
//    } else {
//        internetStatus = "Lost Internet Connection"
//    }
//
//
//
//    snackbar = Snackbar
//            .make(rootlayout!!, internetStatus, Snackbar.LENGTH_LONG)
//            .setAction("X") { snackbar!!.dismiss() }
//    // Changing message text color
//    snackbar!!.setActionTextColor(Color.WHITE)
//    // Changing action button text color
//    val sbView = snackbar!!.view
//    val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
//    textView.setTextColor(Color.WHITE)
//    if (internetStatus.equals("Lost Internet Connection", ignoreCase = true)) {
//        if (internetConnected) {
//            snackbar!!.show()
//            internetConnected = false
//            hide_internet.setVisibility(View.VISIBLE)
//            hide_internet.setImageResource(R.drawable.ic_wifi)
//        }
//    } else {
//        if (!internetConnected) {
//
//            internetConnected = true
//            snackbar!!.show()
//            hide_internet.setVisibility(View.GONE)
//
//        }
//    }
//
//
//
//
//
//}
//
//
