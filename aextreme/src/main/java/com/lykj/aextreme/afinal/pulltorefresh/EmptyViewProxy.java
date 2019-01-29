package com.lykj.aextreme.afinal.pulltorefresh;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lykj.aextreme.R;

import cn.gl.lib.utils.BaseUtils;


public class EmptyViewProxy implements View.OnClickListener {
    private RelativeLayout loadingRetryView; //总体View
    private onLrpRetryClickListener mListener = null;
    private View loadingView, retryView;
    private TextView loadingViewMsg;
    private TextView retryViewMsg;
    private ImageView imgLion;
    private TextView retryButton;
    private Context context;

    public EmptyViewProxy(Context context) {
        loadingRetryView = (RelativeLayout) View.inflate(context, R.layout.empty_loading_retry, null);
        this.context = context;
        // 加载中
        loadingView = loadingRetryView.findViewById(R.id.lr_proxy_loading);
        loadingViewMsg = (TextView) loadingView.findViewById(R.id.lr_proxy_loading_msg);
        // 重试
        retryView = loadingRetryView.findViewById(R.id.lr_proxy_retry);
        retryViewMsg = (TextView) retryView.findViewById(R.id.lr_proxy_retry_message);
        imgLion = (ImageView) retryView.findViewById(R.id.lr_proxy_img_lion);
        retryButton = (TextView) retryView.findViewById(R.id.lr_proxy_button_retry);
        retryButton.setOnClickListener(this);
        displayLoading();
    }

    public RelativeLayout getProxyView() {
        return loadingRetryView;
    }

    public void setOnRetryClickListener(onLrpRetryClickListener listener) {
        mListener = listener;
    }

    /**
     * Default display loading:
     * You can set background, and message by the following methods
     */
    public void displayLoading() {
        BaseUtils.setVisibleGone(loadingView, retryView);
    }

    public void displayLoading(String msg) {
        displayCustomBkgLoading(msg, R.color.bg);
    }

    public void displayLoading(int msgId) {
        displayCustomBkgLoading(msgId, R.color.bg);
    }

    public void displayCustomBkgLoading(int msgId, int bkgId) {
        loadingViewMsg.setText(msgId);
        loadingView.setBackgroundResource(bkgId);
        BaseUtils.setVisibleGone(loadingView, retryView);
    }

    public void displayCustomBkgLoading(String msg, int bkgId) {
        loadingViewMsg.setText(msg);
        loadingView.setBackgroundResource(bkgId);
        BaseUtils.setVisibleGone(loadingView, retryView);
    }

    /**
     * Display no message for T_RESULT_NO_DATA or certain msg, but not fail.
     *
     */
    public void displayMessage(String msg) {
        displayMessage(msg, false);
    }

    public void displayMessage(int id) {
        displayMessage(id, false);
    }

    public void displayMessage(String msg, boolean retry) {
        BaseUtils.setVisibleGone(retryView, loadingView, imgLion);
        if (!retry) {
            BaseUtils.setGone(retryButton);
        } else {
            BaseUtils.setVisible(retryButton);
            retryButton.setText(R.string.empty_str_retry_btn_refresh);
        }
        retryViewMsg.setText(msg);
    }

    public void displayMessage(int id, boolean retry) {
        displayMessage(context.getString(id), retry);
    }
    /**
     * Des 1.4.null list 展示狮子
     * Create By better on 16/7/19 下午3:47.
     */
    public void displayMessageLion(String msg) {
        BaseUtils.setVisible(retryView,imgLion);
        BaseUtils.setGone(loadingView,retryButton);
        retryViewMsg.setText(msg);
    }

    /**
     * Des 1.4.0需要展示未登录 展示狮子
     * Create By better on 16/7/19 下午3:47.
     */
    public void displayRetryLion(String disPlayMsg) {
        displayRetryLion(disPlayMsg, context.getString(R.string.empty_str_retry));
    }

    public void displayRetryLion(String disPlayMsg, String retryBtnMsg) {
        BaseUtils.setGone(loadingView);
        BaseUtils.setVisible(retryView, imgLion,retryViewMsg, retryButton);
        retryViewMsg.setText(disPlayMsg);
        retryButton.setText(retryBtnMsg);
    }

    /**
     * Display retry message and button
     */
    public void displayRetry() {
        displayRetry(context.getString(R.string.empty_str_retry_hint));
    }
    public void displayRetry(int msgId) {
        displayRetry(context.getString(msgId));
    }

    public void displayRetry(String msg) {
        BaseUtils.setVisibleGone(retryView, loadingView,imgLion);
        BaseUtils.setVisible(retryButton);
        retryViewMsg.setText(msg);
        retryButton.setText(R.string.empty_str_retry);
    }

    public void displayNone() {
        BaseUtils.setGone(retryView, loadingView);
    }

    public interface onLrpRetryClickListener {
        /*public abstract*/ void onRetryClick();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lr_proxy_button_retry) {
            if (null != mListener) {
                mListener.onRetryClick();
            }
        }
    }
}
