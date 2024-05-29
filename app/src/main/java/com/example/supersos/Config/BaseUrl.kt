package com.example.supersos.Config

import com.example.supersos.MainActivity

class BaseUrl {
    companion object {
        private val context = MainActivity.applicationContext()
        val xmlConfigParser = XmlConfigParser(context)
        var IpConfig = xmlConfigParser.getIpConfig()
        var URL: String = "http://$IpConfig:5000"
    }
}
