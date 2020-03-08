package com.ahcj.tbswrap.x5webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm
import com.tencent.smtt.sdk.WebView

class X5WebView : WebView {
    /**上下文 */
    private var mContext: Context? = null

    var x5WebviewCallback: X5WebviewCallback? = null

    var x5WebChromeClient: X5WebChromeClient? = null
        private set
    var x5WebViewClient: X5WebViewClient? = null
        private set

    /**是否可以返回到上一页(true:可以，默认；false：不可以) */
    private var canBackPreviousPage = false
    /**当前Webview所处的上下文（默认大家使用的是DialogFragment） */
    private var mDialog: androidx.fragment.app.DialogFragment? = null
    private var mActivity: Activity? = null
    private var mFragment: androidx.fragment.app.Fragment? = null

    /*
     * 在Code中new实例化一个ew会调用第一个构造函数
     * 如果在xml中定义会调用第二个构造函数
     * 而第三个函数系统是不调用的，要由View（我们自定义的或系统预定义的View）显式调用，一般用于自定义属性的相关操作
     * */
    /**在Java代码中new实例化的时候调用 */
    constructor(context: Context, webcallback: X5WebviewCallback) : super(context) {
        mContext = context
        initWebViewSettings(webcallback)
    }

    /**在xml布局文件中定义的时候调用 */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
//        initWebViewSettings()
    }

    public fun initWebViewSettings(webcallback: X5WebviewCallback?) {
        this.x5WebviewCallback = webcallback
        // settings 的设计
        val webSetting = this.settings

        /*=============================JS的相关设置===========================================*/
        webSetting.javaScriptEnabled = true//设置WebView是否允许执行JavaScript脚本，默认false，不允许。
        webSetting.javaScriptCanOpenWindowsAutomatically =
            true//让JavaScript自动打开窗口，默认false。适用于JavaScript方法window.open()。

        /*=============================缓存机制的相关设置===========================================*/
        webSetting.allowFileAccess =
            true//是否允许访问文件，默认允许。注意，这里只是允许或禁止对文件系统的访问，Assets 和 resources 文件使用file:///android_asset和file:///android_res仍是可访问的。
        webSetting.setAppCacheEnabled(true)//应用缓存API是否可用，默认值false, 结合setAppCachePath(String)使用。
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)//设置app缓存容量
        webSetting.setAppCachePath(mContext!!.applicationContext.getDir("appcache", 0).path)//设置缓存路径
        Log.e(TAG, "{webview设置缓存路径==}" + mContext!!.applicationContext.getDir("appcache", 0).path)
        //		webSetting.setDatabaseEnabled(true);//数据库存储API是否可用，默认值false。
        //		webSetting.setDatabasePath(mContext.getApplicationContext().getDir("databases", 0).getPath());
        webSetting.domStorageEnabled = true// 使用localStorage则必须打开, 支持文件存储
        webSetting.setGeolocationEnabled(true)
        webSetting.cacheMode =
            WebSettings.LOAD_CACHE_ELSE_NETWORK//只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
        webSetting.setGeolocationDatabasePath(
            mContext!!.applicationContext.getDir("geolocation", 0)
                .path
        )

        /*==============================webview页面自适应屏幕的相关设置===========================================*/
        webSetting.layoutAlgorithm =
            LayoutAlgorithm.NARROW_COLUMNS// 排版适应屏幕 设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
        webSetting.setSupportZoom(true)//是否应该支持使用其屏幕缩放控件和手势缩放,默认值true
        webSetting.builtInZoomControls = true//设置触摸可缩放  ，默认值为false。
        webSetting.useWideViewPort = true// 设置此属性，可任意比例缩放。
        // webSetting.setLoadWithOverviewMode(true);//是否允许WebView度超出以概览的方式载入页面，默认false。即缩小内容以适应屏幕宽度

        webSetting.setSupportMultipleWindows(false)//设置WebView是否支持多窗口。如果设置为true，主程序要实现onCreateWindow(WebView, boolean, boolean, Message)，默认false。

        webSetting.defaultTextEncodingName = ENCODENAME//设置网页默认编码

        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        //		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_DEFAULT
        // 设置缓存模式【我们可以在有网的情况下将缓存模式改为websetting.setCacheMode(WebSettings.LOAD_DEFAULT);当没有网络时则设置为 LOAD_CACHE_ELSE_NETWORK】
        //		if(HttpUtil.isNetworkAvailable(mContext)){
        //			webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        //		}else{
        //			webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//第一次加载之后便在本地缓存，如果没网络就加载缓存，
        //		}

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension

        //在接入SDK后，需要放到创建X5的WebView之后（也就是X5内核加载完成）进行；否则，cookie的相关操作只能影响系统内核。
        //		CookieSyncManager.createInstance(mContext);
        //		CookieSyncManager.getInstance().sync();

        //使用WebChormClient的特性处理html页面
        x5WebChromeClient = X5WebChromeClient(context, this, x5WebviewCallback)
        this.webChromeClient = x5WebChromeClient
        //使用WebViewClient的特性处理html页面
        x5WebViewClient = X5WebViewClient(context, x5WebviewCallback)
        this.webViewClient = x5WebViewClient

        //实现html文件中可以调用java方法
        addJavascriptInterface(X5WebViewJSInterface.getInstance(mContext, this), "appMethodCanBack")


        /**
         * 对于一些下载的链接，比如apk文件，直接跳转app的系统浏览器
         */
        setDownloadListener { url, s1, s2, s3, l ->
            Log.i("ddddddddddd", "${url}")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            mContext?.startActivity(intent)
        }

    }


    /**加载远程网页
     * @param webUrl - 远程网页链接地址：比如http://m.baidu.com/
     */
    fun loadWebUrl(webUrl: String) {
        this.loadUrl(webUrl)
    }

    /**加载本地assets目录下的网页
     * @param localUrl - assets目录下的路径：比如www/login.html
     */
    fun loadLocalUrl(localUrl: String) {
        this.loadUrl("file:///android_asset/$localUrl")
    }

    /**设置是否直接退出还是返回上一页面【根据实际情况，可以再返回当前webview的载体（activity或者DialogFragment）进行处理】 */
    fun setCanBackPreviousPage(
        canBackPreviousPage: Boolean,
        dialog: androidx.fragment.app.DialogFragment
    ) {
        this.canBackPreviousPage = canBackPreviousPage
        this.mDialog = dialog
    }

    fun setCanBackPreviousPage(canBackPreviousPage: Boolean, activtiy: Activity) {
        this.canBackPreviousPage = canBackPreviousPage
        this.mActivity = activtiy
    }

    fun setCanBackPreviousPage(
        canBackPreviousPage: Boolean,
        fragment: androidx.fragment.app.Fragment
    ) {
        this.canBackPreviousPage = canBackPreviousPage
        this.mFragment = fragment
    }

    /**按返回键时， 是不退出当前界面而是返回上一浏览页面还是直接退出当前界面 */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.e(TAG, "{onKeyDown}canBackPreviousPage=$canBackPreviousPage")
        if (canBackPreviousPage) {
            if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                goBack()
                return true
            } else if (keyCode == KeyEvent.KEYCODE_BACK && !canGoBack()) {
                //当没有上一页可返回的时候
                //此处执行DialogFragment关闭或者Activity关闭.....
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
                return !(mActivity != null || mFragment != null)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {

        private val TAG = X5WebView::class.java.simpleName
        /**网页编码 */
        private val ENCODENAME = "utf-8"
    }


    interface X5WebviewCallback {
        //加载新的页面拦截判断
        fun shouldOverride(view: WebView?, url: String?): Boolean

        //页面加载完成回调
        fun onPageFinished()

        //通过接口的形式添加webchromeClent
        fun progressChange(newProgress: Int)

        //设置标题
        fun onSetWebTitle(title: String?)
    }


}
