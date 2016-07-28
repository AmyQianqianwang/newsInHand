package com.newsinhand.shownews;

import com.example.newsinhand.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class Webview extends Activity {
    WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);  
		setContentView(R.layout.webview);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
		Intent intent=getIntent();
		String link=(String)intent.getSerializableExtra("news_link");
	    String sort = (String) intent.getSerializableExtra("news_sort");
	    TextView title_text=(TextView)findViewById(R.id.Web_title_text);
		title_text.setText(sort);
		webView=(WebView)findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		
		//自适应屏幕
		webView.getSettings().setUseWideViewPort(true); 
		webView.getSettings().setLoadWithOverviewMode(true); 
		
		webView.loadUrl(link);
		webView.setWebViewClient(new HelloWebViewClient());
		ImageButton backBtn = (ImageButton) this.findViewById(R.id.Web_back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	private class HelloWebViewClient extends WebViewClient{
		public boolean shouldOverrideUriLoading(WebView view,String url){
			view.loadUrl(url); 
			return true;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()){
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newsview, menu);
		return true;
	}

}
