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
        mX5WebView?.loadWebUrl("http://api.coloan.cn/mk/classDetails?productId=62&userId=99&source=1&token=eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAB2NQQ7CIBQF7_LXkAAVWrp15R3YQKGKqYUIJJqmd_fjcjJv8g541ggzrIu1wgVNuRwtvSixUi3dQL0SE3dyclorIBBthZlLzRWTgxoJlOawLt9Sw6v7UhCjD3ZDss0jLa3U1GX45H8sBGe8x1u6x522Et44OwzYnK_JB4MrxhgxkB9p72iAT4PETzZKZQBNj24eldYnnD-8XUhqxgAAAA.j3R5BCTOBs3D9KO0AAQUFAJpDH7Jojq7Tau7LNtkybI")
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
