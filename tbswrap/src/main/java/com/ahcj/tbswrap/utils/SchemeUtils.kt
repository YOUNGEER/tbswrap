package com.ahcj.tbswrap.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 *@package:com.cl.idaike.webview.utils
 *@data on:2019/5/8 10:54
 *author:YOUNG
 *desc:TODO
 */
object SchemeUtils {
    fun startActivity(context: Context, url: String?): Boolean {
        try {
            if (url?.startsWith("http") == true) {//正常的网页链接
                return false
            } else {//
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))//scheme跳转
                context.startActivity(intent)
                return true
            }
        } catch (e: Exception) {
            return true
        }
    }

}