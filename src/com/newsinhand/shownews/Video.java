package com.newsinhand.shownews;

import com.example.newsinhand.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Video extends Activity
{
	//WebView webView = (WebView) this.findViewById(R.id.video_webview);

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.video);
		WebView webView = (WebView) this.findViewById(R.id.video_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://video.sina.cn/news?vt=4&PHPSESSID=drofeo1n0aho7qgqp2ke7hihb6&clicktime=1431830111037&userid=user143183011103742901346483267844");
		webView.setWebViewClient(new WebViewClient() {
	        public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                    view.loadUrl(url);
                    return true;
            }
       });
		//System.out.println(news_id);		
	}
	/*protected void onPause ()
	    {
	        webView.reload ();

	        super.onPause ();
	    }*/
}
