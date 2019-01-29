package com.lykj.aextreme.afinal.pulltorefresh;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lykj.aextreme.R;

/**
 * 列表Footer显示提示信息
 * @author 花佟林雨月
 *
 */
public class FooterLastViewProxy {
	/** footer 整体布局 */
	private FrameLayout footerLastView;
	/** 文字提醒父布局 */
	private LinearLayout lastHintTextRegion;
	/** 文字提醒 */
	private TextView lastHintText;
	private Context context;
	
	public FooterLastViewProxy(Context context) {
		footerLastView = (FrameLayout) View.inflate(context, R.layout.footer_last, null);
		lastHintTextRegion = (LinearLayout) footerLastView.findViewById(R.id.footer_last_hint_text_region);
		lastHintText = (TextView) lastHintTextRegion.findViewById(R.id.footer_last_hint_text);
		this.context = context;
		displayNone();
	}
	
	public FrameLayout getProxyView() {
		return footerLastView;
	}
	
	/**
	 * 隐藏整个Footer
	 */
	public void displayNone() {
		lastHintTextRegion.setVisibility(View.GONE);
	}
	
	/**
	 * 显示默认提示文字
	 */
	public void displayHintText() {
		displayHintText(R.string.footer_last_hint_info);
	}
	
	/**
	 * 显示提示文字
	 * @param textId 文字在资源文件中的string id
	 */
	public void displayHintText(int textId) {
		displayHintText(context.getString(textId));
	}
	
	/**
	 * 显示提示文字
	 * @param text 提示文字字符串
	 */
	public void displayHintText(String text) {
		if(lastHintTextRegion.getVisibility() != View.VISIBLE) {
			lastHintTextRegion.setVisibility(View.VISIBLE);
		}
		lastHintText.setText(text);
	}
}
