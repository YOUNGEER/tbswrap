package com.ahcj.tbsdemo

import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.TextView
import com.ahcj.tbswrap.SimpleCommonActivity
import com.ahcj.tbswrap.x5webview.X5WebView
import com.tencent.smtt.sdk.WebView

class MainActivity : SimpleCommonActivity(), X5WebView.X5WebviewCallback {
    override fun addTitleView(): View {
        return TextView(this)
    }

    override fun initDatas() {
        mX5WebView?.initWebViewSettings(this)
        mX5WebView?.addJavascriptInterface(this, "image")
        mX5WebView?.loadWebUrl("https://www.youku.com")
    }

    override fun initEvents() {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun shouldOverride(view: WebView?, url: String?): Boolean {
        return false
    }

    override fun onPageFinished() {

    }

    override fun progressChange(newProgress: Int) {

    }

    override fun onSetWebTitle(title: String?) {

    }


    /**
     * 图片放大
     */
    @JavascriptInterface
    fun openImage(position: Int, array: ArrayList<String>, share: Boolean = true) {

        Log.i("Fasdfsadfsadfasdf", "111111111")
    }
}
