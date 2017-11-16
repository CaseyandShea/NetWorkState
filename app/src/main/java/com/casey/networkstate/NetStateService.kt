package com.casey.networkstate

import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log


/**
 * Created by Casey on 2017/11/16
 *Fuction 网络连接相关的操作管理类
 */
class NetStateService : Service() {
    lateinit var connectivityManager: ConnectivityManager
    lateinit var info: NetworkInfo

    var status = 0
    val TAG = "network"
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("net", "收到广播")
            val mIntent = Intent()
            mIntent.action = "netWorkChange"
            val action = intent!!.action
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivityManager.activeNetworkInfo != null) {
                    // 获取当前网络状态信息
                    info = connectivityManager.activeNetworkInfo
                    if (info.isAvailable) {
                        Log.e(TAG, info.typeName)
                        if (info.type == ConnectivityManager.TYPE_WIFI) {
                            status = 2


                        } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                            status = 1
                            mIntent.putExtra("moblie", getNetWorkClass(this@NetStateService))
                        }
                    } else {
                        status = 0
                    }
                } else {
                    status = 0
                }
                mIntent.putExtra("status", status)
                //发送网络变化广播
                sendBroadcast(mIntent)
            }
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        //注册网络状态的广播，绑定到mReceiver
        val mFilter = IntentFilter()
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mReceiver, mFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }


    companion object {
        /**
         * 判断网络是否可用
         */
        fun isNetworkAvailable(context: Context): Boolean {
            // 获取网络连接管理器
            val mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // 获取当前网络状态信息
            val info = mgr.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }

            return false
        }

        /**
         * 获取当前网络类型
         */
        fun getNetWorkType(context: Context): Int {
            var netWorkType = NETWORK_CLASS_UNKNOWN

            val connectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                val type = networkInfo.type

                if (type == ConnectivityManager.TYPE_WIFI) {
                    netWorkType = NETWORK_WIFI
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    netWorkType = getNetWorkClass(context)
                }
            }

            return netWorkType
        }

        /**
         * 获取2G/3G/4G
         */
        fun getNetWorkClass(context: Context): Int {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            when (telephonyManager.networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> return NETWORK_CLASS_2_G

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> return NETWORK_CLASS_3_G

                TelephonyManager.NETWORK_TYPE_LTE -> return NETWORK_CLASS_4_G

                else -> return NETWORK_CLASS_UNKNOWN
            }
        }

    }
}