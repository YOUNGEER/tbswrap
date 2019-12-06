package com.ahcj.tbswrap.x5webview

import android.content.Context
import android.net.Uri
import android.util.Log

import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView


/**
 * Used 处理解析，渲染网页等浏览器做的事情。辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
 */
class X5WebChromeClient(
    /**依赖的窗口 */
    private val context: Context,
    private val x5WebView: X5WebView,
    private val x5Callbak: X5WebView.X5WebviewCallback?
) : WebChromeClient() {


    /*

    /*=========================================实现webview打开文件管理器功能==============================================*/
/**
 * HTML界面：
 * <input accept="image/*" capture="camera" id="imgFile" name="imgFile" type="file">
 * <input type="file" capture="camera" accept="* /*" name="image">
 *  */

/**
 * 重写WebChromeClient 的openFileChooser方法
 * 这里有个漏洞，4.4.x的由于系统内核发生了改变，没法调用以上方法，现在仍然找不到解决办法，唯一的方法就是4.4直接使用手机浏览器打开，这个是可以的。
 *
*/

     */
     */
     */


    private var mUploadMessage: android.webkit.ValueCallback<Uri>? = null//5.0--版本用到的
    private var mUploadCallbackAboveL: android.webkit.ValueCallback<Array<Uri>>? = null//5.0++版本用到的


    //更改加载进度值
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        x5Callbak?.progressChange(newProgress)
    }

    override fun onReceivedTitle(p0: WebView?, p1: String?) {
        super.onReceivedTitle(p0, p1)
        x5Callbak?.onSetWebTitle(p1)
    }

    //3.0--
    fun openFileChooser(uploadMsg: android.webkit.ValueCallback<Uri>) {
        openFileChooserImpl(uploadMsg)
    }

    //3.0++
    fun openFileChooser(uploadMsg: android.webkit.ValueCallback<Uri>, acceptType: String) {
        openFileChooserImpl(uploadMsg)
    }

    //4.4--(4.4.2特殊，不执行该方法)
    fun openFileChooser(
        uploadMsg: android.webkit.ValueCallback<Uri>,
        acceptType: String,
        capture: String
    ) {
        openFileChooserImpl(uploadMsg)
    }

    //5.0++
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        openFileChooserImplForAndroid5(filePathCallback)
        return true
    }

    //5.0--的调用
    private fun openFileChooserImpl(uploadMsg: android.webkit.ValueCallback<Uri>) {
        mUploadMessage = uploadMsg
        dispatchTakePictureIntent()
    }

    //5.0++的调用
    private fun openFileChooserImplForAndroid5(uploadMsg: android.webkit.ValueCallback<Array<Uri>>?) {
        mUploadCallbackAboveL = uploadMsg
        dispatchTakePictureIntent()
    }

    //拍照或者打开文件管理器
    private fun dispatchTakePictureIntent() {
        if (mUploadMessage != null) {
            Log.w(TAG, "mUploadMessage.toString()=" + mUploadMessage!!.toString())
        }
        if (mUploadCallbackAboveL != null) {
            Log.w(TAG, "mUploadCallbackAboveL.toString()=" + mUploadCallbackAboveL!!.toString())
        }
        X5WebViewJSInterface.getInstance(context, x5WebView).chooseFile()
    }

    fun getmUploadMessage(): android.webkit.ValueCallback<Uri>? {
        return mUploadMessage
    }

    fun getmUploadCallbackAboveL(): android.webkit.ValueCallback<Array<Uri>>? {
        return mUploadCallbackAboveL
    }

    fun setmUploadMessage(mUploadMessage: android.webkit.ValueCallback<Uri>) {
        this.mUploadMessage = mUploadMessage
    }

    fun setmUploadCallbackAboveL(mUploadCallbackAboveL: android.webkit.ValueCallback<Array<Uri>>) {
        this.mUploadCallbackAboveL = mUploadCallbackAboveL
    }

    companion object {

        private val TAG = X5WebChromeClient::class.java.simpleName
    }


}
