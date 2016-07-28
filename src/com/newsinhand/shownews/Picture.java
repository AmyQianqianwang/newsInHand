package com.newsinhand.shownews;

import com.example.newsinhand.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Picture extends Activity
{

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pic);
		WebView webView = (WebView) this.findViewById(R.id.picture_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://photo.sina.cn/?ttp=navmeitu&vt=4&pos=108");
		webView.setWebViewClient(new WebViewClient() {
	        public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                    view.loadUrl(url);
                    return true;
            }
       });
		//System.out.println(news_id);			
	}
}
