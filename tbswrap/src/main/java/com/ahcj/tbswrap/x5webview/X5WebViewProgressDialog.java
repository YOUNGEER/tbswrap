package com.ahcj.tbswrap.x5webview;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.ahcj.tbswrap.R;


/**
 * @Created HaiyuKing
 * @Used  网页加载时的进度对话框
 */
public class X5WebViewProgressDialog extends AlertDialog {

	/**对话框依赖的窗口*/
	private Context context;
	private ImageView img;

	/**
	 * 使用固定样式*/
	public X5WebViewProgressDialog(Context context) {
		this(context, R.style.x5webview_loading_style);
	}
	/**
	 * 使用指定样式*/
	public X5WebViewProgressDialog(Context context, int theme) {
		super(context, theme);

		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.x5webview_dialog_webviewprogress);//引用布局文件

		//设置为false，按对话框以外的地方不起作用
		setCanceledOnTouchOutside(true);
		//设置为false，按返回键不能退出
		setCancelable(true);

		img=findViewById(R.id.img);

		RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		LinearInterpolator lin = new LinearInterpolator();
		rotate.setInterpolator(lin);
		rotate.setDuration(2000);//设置动画持续周期
		rotate.setRepeatCount(-1);//设置重复次数
		rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
		rotate.setStartOffset(10);//执行前的等待时间
		img.setAnimation(rotate);

	}
}
