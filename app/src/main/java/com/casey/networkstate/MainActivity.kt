package com.casey.networkstate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var mintent: Intent? = null
    lateinit var mReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mintent = Intent(this@MainActivity, NetStateService::class.java)
        mintent!!.action = "com.minitor.network"
        startService(mintent)
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {

                    when (intent.getIntExtra("status", 0)) {
                        0 -> network.setBackgroundResource(R.mipmap.no_wifi)
                        1 -> {
                            when (intent.getIntExtra("moblie", 0)) {
                                NETWORK_CLASS_2_G -> network.setBackgroundResource(R.mipmap.g_2)
                                NETWORK_CLASS_3_G -> network.setBackgroundResource(R.mipmap.g_3)
                                NETWORK_CLASS_4_G -> network.setBackgroundResource(R.mipmap.g_4)
                            }
                        }
                        2 -> network.setBackgroundResource(R.mipmap.wifi)
                    }
                }
            }

        }
        registerReceiver(mReceiver, IntentFilter("netWorkChange"))
        intData()
    }

    private fun intData() {
        when (NetStateService.getNetWorkType(this@MainActivity)) {
            1 -> network.setBackgroundResource(R.mipmap.wifi)
            2 -> network.setBackgroundResource(R.mipmap.g_2)
            3 -> network.setBackgroundResource(R.mipmap.g_3)
            4 -> network.setBackgroundResource(R.mipmap.g_4)
            0 -> network.setBackgroundResource(R.mipmap.no_wifi)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mintent != null) {

            stopService(mintent)
        }
        unregisterReceiver(mReceiver)
    }
}
