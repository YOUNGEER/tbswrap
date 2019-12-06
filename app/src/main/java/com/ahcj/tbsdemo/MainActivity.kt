package com.ahcj.tbsdemo

import android.content.Intent
import com.ahcj.tbswrap.FilechooserActivity

class MainActivity : FilechooserActivity() {

    override fun initDatas() {

        mX5WebView?.loadWebUrl("https://kefu.easemob.com/webim/im.html?configId=0ca19323-9d81-4c31-88b0-89e73af246b9&hideKeyboard=true")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
