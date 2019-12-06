package com.ahcj.tbswrap

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.ahcj.tbswrap.utils.GetPathFromUri4kitkat
import com.ahcj.tbswrap.utils.SchemeUtils
import com.ahcj.tbswrap.utils.WebviewGlobals
import com.ahcj.tbswrap.x5webview.X5WebView
import com.ahcj.tbswrap.x5webview.X5WebViewJSInterface
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.activity_x5webview.*
import java.io.File

/**
 * Used 用于展示在web端<input type=text></input>的标签被选择之后，文件选择器的制作和生成
 */

abstract class FilechooserActivity : AppCompatActivity(), X5WebView.X5WebviewCallback {

    override fun shouldOverride(view: WebView?, url: String?): Boolean {
        return SchemeUtils.startActivity(this, url)
    }

    override fun onPageFinished() {
        myProgressBar.visibility = View.INVISIBLE
    }

    override fun onSetWebTitle(title: String?) {
        toolbar_title.text = title
    }

    override fun progressChange(newProgress: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            myProgressBar.setProgress(newProgress, true)
        } else {
            myProgressBar.progress = newProgress
        }
    }

    //内容显示区域
    var mX5WebView: X5WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_x5webview)

        initViews()
        initDatas()
        initEvents()
    }

    public override fun onDestroy() {
        if (mX5WebView != null) {
            mX5WebView!!.removeAllViews()
            mX5WebView!!.destroy()
        }

        super.onDestroy()
    }


    private fun initViews() {

        mX5WebView = X5WebView(this, this)
        center_layout.addView(
            mX5WebView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        mX5WebView!!.setCanBackPreviousPage(true, this@FilechooserActivity)//设置可返回上一页


        iv_close.setOnClickListener { finish() }
        iv_back.setOnClickListener { backEnter() }

    }

    abstract fun initDatas()


    private fun initEvents() {

    }

    /**
     * 截取返回软键事件【在activity中写，不能在自定义的X5Webview中】
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backEnter()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    fun backEnter() {
        if (mX5WebView?.canGoBack() == true) {
            mX5WebView?.goBack()
        } else {
            this.finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //webview界面调用打开本地文件管理器选择文件的回调
            if (requestCode == WebviewGlobals.CHOOSE_FILE_REQUEST_CODE) {
                val result = data?.data
                Log.w(TAG, "{onActivityResult}文件路径地址：" + result!!.toString())

                //如果mUploadMessage或者mUploadCallbackAboveL不为空，代表是触发input[type]类型的标签
                if (null != mX5WebView!!.x5WebChromeClient!!.getmUploadMessage() || null != mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL()) {
                    if (mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL() != null) {
                        onActivityResultAboveL(requestCode, data)//5.0++
                    } else if (mX5WebView!!.x5WebChromeClient!!.getmUploadMessage() != null) {
                        mX5WebView!!.x5WebChromeClient!!.getmUploadMessage()!!.onReceiveValue(result)//将文件路径返回去，填充到input中
                        mX5WebView!!.x5WebChromeClient!!.setmUploadMessage(null!!)
                    }
                } else {
                    //此处代码是处理通过js方法触发的情况
                    Log.w(TAG, "{onActivityResult}文件路径地址(js)：$result")
                    val filePath = GetPathFromUri4kitkat.getPath(
                        this@FilechooserActivity,
                        Uri.parse(result.toString())
                    )

                    //修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
                    setUrlPathInput(mX5WebView, "打开本地相册：" + filePath!!)
                }
            }
            //因为拍照指定了路径，所以data值为null
            if (requestCode == WebviewGlobals.CAMERA_REQUEST_CODE) {
                val pictureFile = File(X5WebViewJSInterface.mCurrentPhotoPath)

                val uri = Uri.fromFile(pictureFile)
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = uri
                this@FilechooserActivity.sendBroadcast(intent)  // 这里我们发送广播让MediaScanner 扫描我们制定的文件
                // 这样在系统的相册中我们就可以找到我们拍摄的照片了【但是这样一来，就会执行MediaScanner服务中onLoadFinished方法，所以需要注意】

                //拍照
                //				String fileName = FileUtils.getFileName(X5WebViewJSInterface.mCurrentPhotoPath);
                Log.e(
                    TAG,
                    "WebViewJSInterface.mCurrentPhotoPath=" + X5WebViewJSInterface.mCurrentPhotoPath
                )

                //修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
                setUrlPathInput(mX5WebView, "打开相机：" + X5WebViewJSInterface.mCurrentPhotoPath)
            }

            //录音
            if (requestCode == WebviewGlobals.RECORD_REQUEST_CODE) {
                val result = data?.data
                Log.w(
                    TAG,
                    "录音文件路径地址：" + result!!.toString()
                )//录音文件路径地址：content://media/external/audio/media/111

                val filePath = GetPathFromUri4kitkat.getPath(
                    this@FilechooserActivity,
                    Uri.parse(result.toString())
                )
                Log.w(TAG, "录音文件路径地址：" + filePath!!)

                //修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
                setUrlPathInput(mX5WebView, "打开录音：$filePath")
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {//resultCode == RESULT_CANCELED 解决不选择文件，直接返回后无法再次点击的问题
            if (mX5WebView!!.x5WebChromeClient!!.getmUploadMessage() != null) {
                mX5WebView!!.x5WebChromeClient!!.getmUploadMessage()!!.onReceiveValue(null)
                mX5WebView!!.x5WebChromeClient!!.setmUploadMessage(null!!)
            }
            if (mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL() != null) {
                mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL()!!.onReceiveValue(null)
                mX5WebView!!.x5WebChromeClient!!.setmUploadCallbackAboveL(null!!)
            }
        }
    }

    //5.0以上版本，由于api不一样，要单独处理
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, data: Intent?) {

        if (mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL() == null) {
            return
        }
        var result: Uri? = null
        if (requestCode == WebviewGlobals.CHOOSE_FILE_REQUEST_CODE) {//打开本地文件管理器选择图片
            result = data!!.data
        } else if (requestCode == WebviewGlobals.CAMERA_REQUEST_CODE) {//调用相机拍照
            val pictureFile = File(X5WebViewJSInterface.mCurrentPhotoPath)

            val uri = Uri.fromFile(pictureFile)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = uri
            this@FilechooserActivity.sendBroadcast(intent)  // 这里我们发送广播让MediaScanner 扫描我们制定的文件
            // 这样在系统的相册中我们就可以找到我们拍摄的照片了【但是这样一来，就会执行MediaScanner服务中onLoadFinished方法，所以需要注意】

            result = Uri.fromFile(pictureFile)
        }
        Log.w(TAG, "{onActivityResultAboveL}文件路径地址：" + result!!.toString())
        mX5WebView!!.x5WebChromeClient!!.getmUploadCallbackAboveL()!!.onReceiveValue(arrayOf(result))//将文件路径返回去，填充到input中
        mX5WebView!!.x5WebChromeClient!!.setmUploadCallbackAboveL(null!!)
        return
    }

    //设置网页上的文件路径输入框文本
    private fun setUrlPathInput(webView: X5WebView?, urlPath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView!!.evaluateJavascript("setInputText('$urlPath')") { value ->
                Log.i(
                    TAG,
                    "onReceiveValue value=$value"
                )
            }
        } else {
            Toast.makeText(
                this@FilechooserActivity,
                "当前版本号小于19，无法支持evaluateJavascript，需要使用第三方库JSBridge",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private val TAG = FilechooserActivity::class.java.simpleName
    }
}
