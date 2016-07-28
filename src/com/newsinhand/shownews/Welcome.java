package com.newsinhand.shownews;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;


import com.example.newsinhand.R;
import com.newsinhand.shownews.Main;
import com.newsinhand.shownews.Welcome;
import com.newsinhand.database.DB;
import com.newsinhand.database.PicDB;
import com.newsinhand.definition.Pic;
import com.newsinhand.definition.News;
import com.newsinhand.fileoperate.FileOper;
import com.newsinhand.fileoperate.SetCode;
import com.newsinhand.fileoperate.SaveImage;
public class Welcome  extends Activity {
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/news_image/" + "yaowen/";
	private List<News> mList = new ArrayList<News>();
	private FileOper tool = new FileOper();
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
/*		DigitalClock digitalClock = (DigitalClock) this
				.findViewById(R.id.welcome_time);
		// 文字大小30
		digitalClock.setTextSize(20);
		// 文字颜色
		digitalClock.setTextColor(Color.WHITE);*/
		
		
		File dirFile = new File(Environment
				.getExternalStorageDirectory() + "/news_image/");
		if (!dirFile.exists()) {
			dirFile.mkdir();
			System.out.println("创建文件夹:news_image");
		}
		
		

		PicDB db = new PicDB(Welcome.this);
		List<Pic> pic = new ArrayList<Pic>();
		pic = db.check();
		if(pic.isEmpty()){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						SetCode data = new SetCode();
						@SuppressWarnings("static-access")
						String html = data
								.getHtmlByUrl("http://news.sina.com.cn/");
						if (html != null && !"".equals(html)) {
							Document doc = Jsoup.parse(html);
							Elements div = doc
									.select("ul#Blk01_Focus_Cont");
							Elements data_xl = div.select("li");
							List<Pic> pic = new ArrayList<Pic>();
							for (int i = 0; i < data_xl.size(); i++) {
								Pic ns = new Pic();
								System.out.println("1"
										+ data_xl.get(i).select("div.text")
												.text());
								System.out.println("2"
										+ data_xl.get(i).select(".pic img")
												.attr("src"));
								System.out.println("3"
										+ data_xl.get(i).select(".pic")
												.attr("href"));
								Time t = new Time();
								t.setToNow(); // 取得系统时间。
								int year = t.year;
								int month = t.month + 1;
								int monthday = t.monthDay;
								int hour = t.hour;
								int minute = t.minute;
								final String title;
								final String image;
								String link, date, store;
								title = data_xl.get(i).select("div.text")
										.text();
								image = data_xl.get(i).select(".pic img")
										.attr("src");
								link = data_xl.get(i).select(".pic")
										.attr("href");
								date = year + "-" + month + "-" + monthday
										+ " " + hour + ":" + minute;
								store = Environment
										.getExternalStorageDirectory()
										+ "/news_image/"
										+ "Nav/"
										+ title
										+ ".jpeg";
								ns.setTitle(title);
								ns.setImage(image);
								ns.setLink(link);
								ns.setDate(date);
								ns.setStore(store);
								pic.add(ns);
							}
							PicDB db = new PicDB(Welcome.this);
							db.delete();
							db.insert(pic);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 下载导航栏图片
					PicDB db = new PicDB(Welcome.this);
					List<Pic> pic = new ArrayList<Pic>();
					pic = db.check();
					for (int i = 0; i < 5; i++) {
						Pic ns = new Pic();
						ns = pic.get(i);
						final String image = ns.getImage();
						final String title = ns.getTitle();
						// 将网络图片存放在本地
						if (!tool.isExist("news_image/Nav/" + title
								+ ".jpeg")) {
							try {
								URL url = new URL(image);
								InputStream is = url.openStream();
								bitmap = BitmapFactory.decodeStream(is);
								SaveImage save = new SaveImage();
								save.saveFile(
										bitmap,
										Environment
												.getExternalStorageDirectory()
												+ "/news_image/" + "Nav/",
										title + ".jpeg");
								is.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}
			}).start();
		 }
						
		
		List<News> db_data = new ArrayList<News>();
		DB news_db = new DB(Welcome.this);
		db_data = news_db.check("要闻");
		if (db_data.isEmpty() || db_data.size() < 10) {
			new Thread(new Runnable() {
				@TargetApi(Build.VERSION_CODES.GINGERBREAD)
				@Override
				public void run() {
					try {
						Document doc = Jsoup.connect("http://news.qq.com/")
								.get();
						Elements div_1 = doc.select(".Q-tpWrap");
						for (int i = 0; i < div_1.size(); i++) {
							News ns = new News();
							Time t = new Time();
							t.setToNow(); // 取得系统时间。
							int year = t.year;
							int month = t.month + 1;
							int monthday = t.monthDay;
							int hour = t.hour;
							int minute = t.minute;
							final String title;
							String desc;
							final String image;
							final int id = i;
							String link, date, sort, refresh, store;
							title = div_1.get(i).select(".linkto").text()
									+ " (要闻)";
							image = div_1.get(i).select(".picto").attr("src");
							link = div_1.get(i).select(".linkto").attr("href");
							date = year + "-" + month + "-" + monthday;
							refresh = year + "-" + month + "-" + monthday + " "
									+ hour + ":" + minute;
							desc = div_1.get(i).getElementsByTag("p").text();
							sort = "要闻";
							store = ALBUM_PATH + title + ".jpeg";
							ns.setTitle(title);
							ns.setImage(image);
							ns.setLink(link);
							ns.setDesc(desc);
							ns.setDate(date);
							ns.setRefresh(refresh);
							ns.setSort(sort);
							ns.setStore(store);
							ns.setId(i);
							if (ns.getImage().isEmpty()
									|| ns.getTitle().isEmpty()
									|| ns.getDesc().isEmpty()) {
								continue;
							}
							mList.add(ns);
						}
						DB db_insert = new DB(Welcome.this);
						db_insert.insert(mList);
						mList.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mList.clear();
					DB news_check = new DB(Welcome.this);
					mList = news_check.check("要闻");

					for (int i = 0; i < mList.size(); i++) {
						News ns = new News();
						ns = mList.get(i);
						final String image = ns.getImage();
						final String title = ns.getTitle();
						// 将网络图片存放在本地
						if (!tool.isExist("news_image/yaowen/" + title
								+ ".jpeg")) {

							try {
								URL url = new URL(image);
								InputStream is = url.openStream();
								bitmap = BitmapFactory.decodeStream(is);
								SaveImage save = new SaveImage();
								bitmap = save.setImage(bitmap);
								save.saveFile(bitmap, ALBUM_PATH, title
										+ ".jpeg");
								is.close();
							} catch (Exception e) {
								// TODO: handle exception
								System.out.println("异常" + e.getMessage());
							}
						}
					}
				}
			}).start();
		}

		final Intent localIntent = new Intent(this, Main.class);
		Timer timer = new Timer();
		TimerTask tast = new TimerTask() {
			@Override
			public void run() {
				startActivity(localIntent);
				finish();
			}
		};
		timer.schedule(tast, 3000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}

