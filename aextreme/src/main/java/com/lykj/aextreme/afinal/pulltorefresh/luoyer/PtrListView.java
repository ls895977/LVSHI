/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.lykj.aextreme.afinal.pulltorefresh.luoyer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.handmark.pulltorefresh.library.OverscrollHelper;
//import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
//import com.handmark.pulltorefresh.library.R;
//import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

import com.lykj.aextreme.R;
import com.lykj.aextreme.afinal.pulltorefresh.OverscrollHelper;
import com.lykj.aextreme.afinal.pulltorefresh.PullToRefreshAdapterViewBase;
import com.lykj.aextreme.afinal.pulltorefresh.internal.EmptyViewMethodAccessor;

import java.security.InvalidParameterException;

/**
 * 实现上下拉刷新的数据列表。
 * 该列表在数据为空的时候会显示EmptyView， {@link #getDefaultEmptyViewProxy()}提供一个对
 * EmptyView的代理，可以通过这个代理类设置其显示内容和样式。使用格式如下：
 * <p>
 * 	<code>
 * 		PtrListView ptrlv = ...;//findViewById(R.id.xxx), etc.	<br/>
 * 		<b><i>ptrlv.getDefaultEmptyViewProxy().</i></b>displayXXX();			<br/>
 * 	</code>
 * </p>
 * @author Muyangmin
 * @create 2014-11-3
 * @version 1.0
 */
public class PtrListView extends PullToRefreshAdapterViewBase<ListView> {

	// set false for the initialization, if set true in xml
	// by ptrScrollingWhileRefreshingEnabled, reset after complete.
	private boolean mScrollingWhileRefreshingEnabled;
	private Mode mMode;
	private DefaultEmptyViewProxy proxy;
	
	private int mPageSize = -1;

	public PtrListView(Context context) {
		super(context);
	}

	public PtrListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScrollingWhileRefreshingEnabled = isScrollingWhileRefreshingEnabled();
		mMode = getMode();
		Log.i(LOG_TAG, "PullToRefreshListView " + mScrollingWhileRefreshingEnabled + " mMode: " + mMode);
	}

	public PtrListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PtrListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}
	
	public void setPageSize(int pageSize) {
		mPageSize = pageSize;
	}
	
	public void initRefreshing() {
		setScrollingWhileRefreshingEnabled(false);
		Log.i(LOG_TAG, "initRefreshing " + isScrollingWhileRefreshingEnabled());
		initListRefreshing();
	}
	
	/**
	 * Refresh success.
	 * @param newListSize new request response list size.
	 */
	public final void onListRefreshComplete(int newListSize) {
		if(-1 == mPageSize) {
			throw new InvalidParameterException("YOU MUST set parameter 'mPageSize'"
					+ " by method 'setPageSize(param)' !");
		}
		onRefreshComplete();
		resetScrollWhileRefreshing();
		ListAdapter adapter = mRefreshableView.getAdapter();
		if(null == adapter || adapter.isEmpty()) {
			setStartMode();
			// set default hint message : no data
			return ;
		}
		if(adapter.getCount() < mPageSize || newListSize < mPageSize) {
			setStartMode();
			return ;
		}
		resetMode();
	}
	
	/**
	 * Refresh fail message
	 * @param failMsg
	 */
	public final void onListRefreshComplete(String failMsg) {
		onRefreshComplete();
		resetScrollWhileRefreshing();
		ListAdapter adapter = mRefreshableView.getAdapter();
		if(null == adapter || adapter.isEmpty()) {
			setStartMode();
			// set hint message
			
		}
		
	}
	
	private void resetScrollWhileRefreshing() {
		if(mScrollingWhileRefreshingEnabled != isScrollingWhileRefreshingEnabled()) {
			setScrollingWhileRefreshingEnabled(mScrollingWhileRefreshingEnabled);
		}
	}
	
	/**
	 * Reset mode to default.
	 * For example:
	 * 	Initialization refresh fail, set to PULL_FROM_START;
	 * 	later, pull to refresh successful, reset to default if not PULL_FROM_START.
	 */
	private void resetMode() {
		if(mMode != getMode()) {
			setMode(mMode);
		}
	}
	
	private void setStartMode() {
		Mode mode = getMode();
		if(mode != Mode.DISABLED && mode != Mode.PULL_FROM_END) {
			setMode(Mode.PULL_FROM_START);
		}
	}
	
	/**
	 * 用于判断这个List是否为空，主要取决于设定的Adapter。
	 * @see #setAdapter(ListAdapter)
	 */
	public boolean isEmpty(){
		ListAdapter adapter = getRefreshableView().getAdapter();
		if (adapter==null){
			return false;
		}
		return adapter.isEmpty();
	}
	
	@Override
	protected void addEmptyView(Context context) {
		proxy = new DefaultEmptyViewProxy(context);
		setEmptyView(proxy.getView());
	}
	
	
	/**
	 * 返回默认View的代理对象。
	 * @return 返回当前EmptyView的一个代理
	 */
	public DefaultEmptyViewProxy getDefaultEmptyViewProxy(){
		return proxy;
	}
	
	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = createListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@TargetApi(9)
	final class InternalListViewSDK9 extends InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PtrListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setEmptyView(View emptyView) {
			PtrListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}

	/**
	 * 处理ListView的空界面。
	 * 客户端调用时需要根据自己的需要调用displayXXX方法。
	 * @author Muyangmin
	 * @create 2014-10-28
	 * @version 1.0
	 */
	public static class DefaultEmptyViewProxy{
		private View view;			//总体EmptyView
		private ContentType currentContentType = null;//当前显示状态，保存这个状态主要是为了优化性能
		//下面五个字段用于保存组件类别，减少从XML中查找的次数。
		private TextView loadtv;
		private ProgressBar loadprg;
		private TextView restv;
		private ImageView resimg;
		private Button resbtn;
		
		
		protected DefaultEmptyViewProxy(Context context){
			view = LayoutInflater.from(context).inflate
					(R.layout.ptr_empty_loading_result, null);
			loadprg = (ProgressBar) view.findViewById(R.id.ptr_lv_loading_prg);
			loadtv = (TextView) view.findViewById(R.id.ptr_lv_loading_tv);
			resbtn = (Button) view.findViewById(R.id.ptr_lv_result_btn);
			resimg = (ImageView) view.findViewById(R.id.ptr_lv_result_img);
			restv = (TextView) view.findViewById(R.id.ptr_lv_result_msg);
		}

		protected View getView(){
			return view;
		}
		
		/**
		 * 考虑扩展性，使用枚举而不是布尔类型定义状态值。
		 */
		private enum ContentType{
			/**
			 * 加载内容时显示。
			 */
			TYPE_LOADING,
			/**
			 * 加载失败或结果为空时显示。
			 */
			TYPE_RESULT,
			/**
			 * 什么都不显示，隐藏这个View。
			 */
			TYPE_NONE
		}
		
		/**
		 * 切换内容，主要是要切换布局中的各种组件的可见性。
		 */
		private void changeContentType(ContentType type){
			//使用当前的状态减少不必要的切换
			if (currentContentType==type){
				return;
			}
			//else, 切换状态
			currentContentType = type;
			switch (type){
				case TYPE_LOADING:
					view.findViewById(R.id.ptr_lv_loading_region).setVisibility(View.VISIBLE);
					view.findViewById(R.id.ptr_lv_result_region).setVisibility(View.GONE);
					break;
				case TYPE_RESULT:
					view.findViewById(R.id.ptr_lv_loading_region).setVisibility(View.GONE);
					view.findViewById(R.id.ptr_lv_result_region).setVisibility(View.VISIBLE);
					break;
				case TYPE_NONE:
					view.setVisibility(View.INVISIBLE);
					break;
				default:break;
			}
		}
		
		/**
		 * 什么都不显示，把EmptyView隐藏。
		 */
		public void displayNothing(){
			changeContentType(ContentType.TYPE_NONE);
		}
		
		/**
		 * 显示加载信息，不显示进度条。
		 */
		public void displayLoading(int strid){
			displayLoading(strid, false);
		}
		
		/**
		 * 显示加载状态，可以控制进度条是否显示
		 * @param strid 显示的文字
		 * @param showProgressBar 是否显示进度条
		 */
		public void displayLoading(int strid, boolean showProgressBar){
			changeContentType(ContentType.TYPE_LOADING);
			loadtv.setText(strid);
			loadtv.setVisibility(View.VISIBLE);
			loadprg.setVisibility(
					showProgressBar? View.VISIBLE : View.INVISIBLE);
		}
		
		public void displayResultMsg(int msgid){
			changeContentType(ContentType.TYPE_RESULT);
			restv.setText(msgid);
			restv.setVisibility(View.VISIBLE);
			resimg.setVisibility(View.INVISIBLE);
			resbtn.setVisibility(View.INVISIBLE);
		}
		
		public void displayResultMsg(CharSequence msg){
			changeContentType(ContentType.TYPE_RESULT);
			restv.setVisibility(View.VISIBLE);
			restv.setText(msg);
			resimg.setVisibility(View.INVISIBLE);
			resbtn.setVisibility(View.INVISIBLE);
		}
		
		/**
		 * 显示一个结果界面，包含文字和图片。
		 * @param msgid 要显示的文字
		 * @param imgid 要显示的图片
		 */
		public void displayResultMsg(int msgid, int imgid){
			changeContentType(ContentType.TYPE_RESULT);
			restv.setText(msgid);
			restv.setVisibility(View.VISIBLE);
			resimg.setImageResource(imgid);
			resimg.setVisibility(View.VISIBLE);
			resbtn.setVisibility(View.INVISIBLE);
		}
		/**
		 * 显示一个结果界面，包含文字和一个按钮。
		 * @param msgid 要显示的文字
		 * @param btnbkg 按钮的背景图片
		 * @param btnListener 按钮的点击事件
		 */
		public void displayResultMsg(int msgid, int btnText, int btnbkg, View.OnClickListener btnListener){
			changeContentType(ContentType.TYPE_RESULT);
			restv.setText(msgid);
			restv.setVisibility(View.VISIBLE);
			
			resimg.setVisibility(View.INVISIBLE);
			
			resbtn.setVisibility(View.VISIBLE);
			resbtn.setText(btnText);
			resbtn.setBackgroundResource(btnbkg);
			resbtn.setOnClickListener(btnListener);
		}
		
		/**
		 * @see #displayResultMsg(int, int, int, View.OnClickListener)
		 */
		public void displayResultMsg(CharSequence msg, int btnText, int btnbkg, View.OnClickListener btnListener){
			changeContentType(ContentType.TYPE_RESULT);
			restv.setText(msg);
			restv.setVisibility(View.VISIBLE);
			
			resimg.setVisibility(View.INVISIBLE);
			
			resbtn.setVisibility(View.VISIBLE);
			resbtn.setText(btnText);
			resbtn.setBackgroundResource(btnbkg);
			resbtn.setOnClickListener(btnListener);
		}
		
		/**
		 * 显示一个结果界面，包含图片、文字和一个按钮，按钮的背景、文字都可以设置
		 * @param msgid 显示的消息文本
		 * @param imgid 显示的图片
		 * @param btnText 按钮显示的文字
		 * @param btnbkg 按钮背景图片
		 * @param btnListener 点击按钮后触发的事件
		 */
		public void displayResultMsg(int msgid, int imgid, int btnText, 
				int btnbkg, View.OnClickListener btnListener){
			changeContentType(ContentType.TYPE_RESULT);
			resimg.setImageResource(imgid);
			restv.setText(msgid);
			resbtn.setBackgroundResource(btnbkg);
			resbtn.setText(btnText);
			resbtn.setOnClickListener(btnListener);
		}
		
	}// end of proxy class
}
