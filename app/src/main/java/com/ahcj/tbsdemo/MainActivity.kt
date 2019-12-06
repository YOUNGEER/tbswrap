package com.ahcj.tbsdemo

import com.ahcj.tbswrap.FilechooserActivity

class MainActivity : FilechooserActivity() {

    override fun initDatas() {

        mX5WebView?.loadWebUrl("https://sj.qq.com/")
    }

}
