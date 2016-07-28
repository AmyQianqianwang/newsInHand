package com.newsinhand.shownews;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.newsinhand.database.DB;
import com.newsinhand.database.PicDB;
import com.newsinhand.definition.Json;
import com.newsinhand.definition.Pic;
import com.newsinhand.definition.News;


import com.newsinhand.fileoperate.FileOper;
import com.newsinhand.fileoperate.SetCode;
import com.newsinhand.fileoperate.SaveImage;
import com.example.newsinhand.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main extends Activity {
	public static boolean isNetworkAvailable = false;
	public static boolean yaowen_state = false, china_state = false,
			global_state = false, sports_state = false, social_state = false,
			joy_state = false, finance_state = false,science_state = false,
			people_state = false,travel_state = false,body_state = false,
			parents_state = false,life_state = false,movie_state = false,pengpai_state = false;

	List<View> listViews;
	String delete_sort = "";
	String json_result = "";
	Bitmap bitmap;
	List<Bitmap> nav_img = new ArrayList<Bitmap>();
	List<String> nav_title = new ArrayList<String>();
	List<String> nav_link = new ArrayList<String>();
	FileOper tool = new FileOper();
	private CheckBox main_stop_btn;
	private SeekBar main_light_value, main_voice_speed;
	private Spinner main_news_sort;
	private Spinner maintext_size, maintext_color;
	Button main_delete, main_out;
	ImageButton voice_open, main_sys,picture_open,video_open;
	//�±ߵĵ�����Image_Button 
	private TextView mContent1;
	PopupWindow popupWindow;
	List<News> mList = new ArrayList<News>();
	View parent;
	Context context = null;
	private DisplayMetrics dm;
	LocalActivityManager manager = null;
	TabHost tabHost = null;
	TabWidget tabWidget = null;
	private ViewPager pager = null;
	TextView tv = null;
	TextView list_title, list_desc;
	int tabid = 0, tabchange = 0;
	private long exitTime = 0;
	private TextToSpeech mSpeech;
	String content = "";
	boolean nav_state = true;
	private ViewPager viewPager; // android-support-v4�еĻ������
	private List<ImageView> imageViews; // ������ͼƬ����

	private String[] titles; // ͼƬ����
	private List<View> dots; // ͼƬ�������ĵ���Щ��

	private TextView tv_title;
	private int currentItem = 0; // ��ǰͼƬ��������
	MyAdapter adapter;
	private ScheduledExecutorService scheduledExecutorService;
	// �л���ǰ��ʾ��ͼƬ
	private Handler handler_nav = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// �л���ǰ��ʾ��ͼƬ
		};
	};
	private Handler handler_ref = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
		};
	};
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			Toast.makeText(Main.this, obj, Toast.LENGTH_SHORT).show();
			return false;
		}
	});
	private Handler handler_voice = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			mSpeech.speak(obj, TextToSpeech.QUEUE_FLUSH, null);
			return false;
		}
	});
	/*private Handler handler_text = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			mContent.setText(obj);
			mSpeech.speak(obj, TextToSpeech.QUEUE_FLUSH, null);
			return false;
		}
	});*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		// �ж��Ƿ���������
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ConnectivityManager connectivityManager = (ConnectivityManager) Main.this
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo mobNetInfoActivity = connectivityManager
							.getActiveNetworkInfo();
					if (mobNetInfoActivity != null) {
						isNetworkAvailable = true;

					} else {
						isNetworkAvailable = false;
					}
				}
			}
		}).start();

		PicDB db = new PicDB(Main.this);
		List<Pic> pic = new ArrayList<Pic>();
		pic = db.check();
		if (pic.size() != 0) {
			for (int i = 0; i < 5; i++) {
				Pic ns = pic.get(i);
				nav_title.add(ns.getTitle());
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(ns.getStore(), options);
				nav_img.add(bitmap);
				nav_link.add(ns.getLink());
			}

			titles = new String[5];
			titles[0] = nav_title.get(0);
			titles[1] = nav_title.get(1);
			titles[2] = nav_title.get(2);
			titles[3] = nav_title.get(3);
			titles[4] = nav_title.get(4);

			imageViews = new ArrayList<ImageView>();

			// ��ʼ��ͼƬ��Դ
			for (int i = 0; i < 5; i++) {
				ImageView imageView = new ImageView(this);
				imageView.setImageBitmap(nav_img.get(i));
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}
		}
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		dots.add(findViewById(R.id.v_dot3));
		dots.add(findViewById(R.id.v_dot4));

		tv_title = (TextView) findViewById(R.id.tv_title);
		//tv_title.setText(titles[0]);

		viewPager = (ViewPager) findViewById(R.id.vp);
		adapter = new MyAdapter();
		viewPager.setAdapter(adapter);// �������ViewPagerҳ���������
		// ����һ������������ViewPager�е�ҳ��ı�ʱ����
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		// ���µ�����
		new Thread(new Runnable() {
			public void run() {
				while (nav_state) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (isNetworkAvailable) {
						try {
							SetCode data = new SetCode();
							@SuppressWarnings("static-access")
							String html = data
									.getHtmlByUrl("http://news.sina.com.cn/");
							System.out.println("sina news");
							if (html != null && !"".equals(html)) {
								Document doc = Jsoup.parse(html);
								Elements div = doc
										.select("ul#Blk01_Focus_Cont");
								Elements data_xl = div.select("li");
								List<Pic> nav = new ArrayList<Pic>();
								for (int i = 0; i < data_xl.size(); i++) {
									Pic ns = new Pic();
									System.out.println("title:"
											+ data_xl.get(i).select("div.text")
													.text());
									System.out.println("image:"
											+ data_xl.get(i).select(".pic img")
													.attr("src"));
									System.out.println("link:"
											+ data_xl.get(i).select(".pic")
													.attr("href"));
									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
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
									nav.add(ns);
									
								}
								PicDB db = new PicDB(Main.this);
								db.delete();
								db.insert(nav);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// ���ص�����ͼƬ
						PicDB db = new PicDB(Main.this);
						List<Pic> pic = new ArrayList<Pic>();
						pic = db.check();
						for (int i = 0; i < 5; i++) {
							Pic ns = new Pic();
							ns = pic.get(i);
							final String image = ns.getImage();
							final String title = ns.getTitle();
							// ������ͼƬ����ڱ���
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

						nav_title.clear();
						nav_img.clear();
						nav_link.clear();

						for (int i = 0; i < 5; i++) {
							Pic ns = pic.get(i);
							nav_title.add(ns.getTitle());
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 1;
							bitmap = BitmapFactory.decodeFile(ns.getStore(),
									options);
							nav_img.add(bitmap);
							nav_link.add(ns.getLink());
						}

						titles = new String[5];
						titles[0] = nav_title.get(0);
						titles[1] = nav_title.get(1);
						titles[2] = nav_title.get(2);
						titles[3] = nav_title.get(3);
						titles[4] = nav_title.get(4);

						imageViews.clear();
						// ��ʼ��ͼƬ��Դ
						for (int i = 0; i < 5; i++) {
							ImageView imageView = new ImageView(
									Main.this);
							imageView.setImageBitmap(nav_img.get(i));
							imageView.setScaleType(ScaleType.CENTER_CROP);
							imageViews.add(imageView);
						}
						handler_ref.obtainMessage().sendToTarget();
						nav_state = false;
					}
				}
			}
		}).start();

		mSpeech = new TextToSpeech(this, new OnInitListener() {
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					int result = mSpeech.setLanguage(Locale.CHINA);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						System.out.println("not use");
					} else {

					}
				}
			}

		});

		yaowen_state = true;
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setup(manager);

		context = Main.this;

		pager = (ViewPager) findViewById(R.id.viewpager);

		listViews = new ArrayList<View>();
		Intent i1 = new Intent(context, Yaowen.class);
		listViews.add(getView("Yaowen", i1));		
		Intent i2 = new Intent(context, Global.class);
		listViews.add(getView("Global", i2));
		Intent i3 = new Intent(context, China.class);
		listViews.add(getView("China", i3));
		Intent i4 = new Intent(context, Science.class);
		listViews.add(getView("Science", i4));
		Intent i5 = new Intent(context, Social.class);
		listViews.add(getView("Social", i5));
		Intent i6 = new Intent(context, Finance.class);
		listViews.add(getView("Finance", i6));	
		Intent i7 = new Intent(context, Joy.class);
		listViews.add(getView("Joy", i7));
		Intent i8 = new Intent(context, Sports.class);
		listViews.add(getView("Sports", i8));
		
		Intent i9 = new Intent(context, Pengpai.class);
		listViews.add(getView("Pengpai", i9));		
		Intent i10 = new Intent(context, People.class);
		listViews.add(getView("People", i10));
		Intent i11 = new Intent(context, Travel.class);
		listViews.add(getView("Travel", i11));
		Intent i12 = new Intent(context, Movie.class);
		listViews.add(getView("Movie", i12));
		Intent i13 = new Intent(context, Parents.class);
		listViews.add(getView("Parents", i13));
		Intent i14 = new Intent(context, Life.class);
		listViews.add(getView("Life", i14));
		Intent i15 = new Intent(context, Body.class);
		listViews.add(getView("Body", i15));

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Ҫ��")
				.setContent(i1));		
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("����")
				.setContent(i2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("����")
				.setContent(i3));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("�Ƽ�")
				.setContent(i4));
		tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("���")
				.setContent(i5));
		tabHost.addTab(tabHost.newTabSpec("tab6").setIndicator("�ƾ�")
				.setContent(i6));
		tabHost.addTab(tabHost.newTabSpec("tab7").setIndicator("����")
				.setContent(i7));	
		tabHost.addTab(tabHost.newTabSpec("tab8").setIndicator("����")
				.setContent(i8));
		tabHost.addTab(tabHost.newTabSpec("tab9").setIndicator("����")
				.setContent(i9));
		tabHost.addTab(tabHost.newTabSpec("tab10").setIndicator("����")
				.setContent(i10));
		tabHost.addTab(tabHost.newTabSpec("tab11").setIndicator("����")
				.setContent(i11));
		tabHost.addTab(tabHost.newTabSpec("tab12").setIndicator("��Ϸ")
				.setContent(i12));
		tabHost.addTab(tabHost.newTabSpec("tab13").setIndicator("����")
				.setContent(i13));
		tabHost.addTab(tabHost.newTabSpec("tab14").setIndicator("����")
				.setContent(i14));
		tabHost.addTab(tabHost.newTabSpec("tab15").setIndicator("����")
				.setContent(i15));
		tabWidget = tabHost.getTabWidget();
		tabWidget.setStripEnabled(false);
		// ��ǩ�ĸ���
		int count = tabWidget.getChildCount();
		// ��ȡ�ֻ���Ļ�Ŀ��
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenheight = displayMetrics.heightPixels;
		if (count >= 5) {
			for (int i = 0; i < count; i++) {
				// ����ÿ����ǩ�Ŀ�ȣ�Ϊ��Ļ��1/5
				tabWidget.getChildTabViewAt(i).setMinimumWidth(
						(screenWidth) / 5);
			}
		}

		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View child = tabWidget.getChildAt(i);
			//child.setBackgroundResource(R.drawable.tabhost_underline_selector);

	        tabWidget.getChildAt(i).setBackgroundResource(R.drawable.tab_indicator_ab_mmstyle);	//new add      
			
	        //singleTabAllGroup.setBackgroundColor(getResources().getColor(R.color.TabPageBackgroudColor));
	        
			tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextSize(18);
			tv.setGravity(Gravity.CENTER);
			TextPaint tp = tv.getPaint();			
			tp.setFakeBoldText(true);
			tv.setTextColor(getResources().getColor(R.color.BLACK2));

		}
		tv = (TextView) tabWidget.getChildAt(0)
				.findViewById(android.R.id.title);
		tv.setTextColor(getResources().getColor(R.color.red));
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(18);
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
		System.out.println("�߳���:" + Thread.activeCount());
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				mList.clear();
				System.out.println("�߳���:" + Thread.activeCount());
				if ("tab1".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("Ҫ��");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://news.qq.com/").get();
									Elements div_1 = doc.select(".Q-tpWrap");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
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
										title = div_1.get(i).select(".linkto")
												.text()
												+ " (Ҫ��)";
										image = div_1.get(i).select(".picto")
												.attr("src");
										link = div_1.get(i).select(".linkto")
												.attr("href");
										date = year + "-" + month + "-"
												+ monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										desc = div_1.get(i)
												.getElementsByTag("p").text();
										sort = "Ҫ��";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "yaowen/"
												+ title + ".jpeg";
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
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("Ҫ��");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/yaowen/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "yaowen/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();

					pager.setCurrentItem(0);
					yaowen_state = true;
					tabid = 0;
					System.out.println("Ҫ��");

					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(16);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				
				if ("tab2".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@SuppressLint("NewApi")
							@Override
							public void run() {
								try {
									Document doc = Jsoup
											.connect(
													"http://news.qq.com/world_index.shtml")
											.get();
									Elements div_1 = doc.select(".Q-tpWrap");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).select(".linkto")
												.text()
												+ " (����)";
										image = div_1.get(i).select(".picto")
												.attr("src");
										link = "http://news.qq.com"
												+ div_1.get(i)
														.select(".linkto")
														.attr("href");
										date = year + "-" + month + "-"
												+ monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										desc = div_1.get(i)
												.getElementsByTag("p").text();
										sort = "����";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "global/"
												+ title
												+ ".jpeg";
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
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/global/"
										+ title + ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "global/",
												title + ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}
						}
					}).start();
					pager.setCurrentItem(1);
					global_state = true;
					tabid = 1;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab3".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup
											.connect(
													"http://news.qq.com/china_index.shtml")
											.get();
									Elements div_1 = doc.select(".Q-tpWrap");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).select(".linkto")
												.text()
												+ " (����)";
										image = div_1.get(i).select(".picto")
												.attr("src");
										link = "http://news.qq.com"
												+ div_1.get(i)
														.select(".linkto")
														.attr("href");
										date = year + "-" + month + "-"
												+ monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										desc = div_1.get(i)
												.getElementsByTag("p").text();
										sort = "����";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "china/"
												+ title + ".jpeg";
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
									System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/china/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "china/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(2);
					china_state = true;
					tabid = 2;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab4".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("�Ƽ�");
					if (db_data.isEmpty() || db_data.size() < 10) {
						HttpURLConnection httpConn = null;
						new Thread(new Runnable() {
							@Override
							public void run() {
								HttpClient client = new DefaultHttpClient();
								// ʹ��Get��ʽ����
								HttpGet httpget = new HttpGet(
										"http://news.open.qq.com/cgi-bin/article.php?site=tech&cnt=36&of=json&callback=jsonp1401022281114&_=1401028188902");
								// ����header��Ϣ
								httpget.setHeader("Accept", "*/*");
								httpget.setHeader("Accept-Encoding",
										"gzip,deflate,sdch");
								httpget.setHeader("Accept-Language",
										"zh-CN,zh;q=0.8");
								httpget.setHeader("Connection", "keep-alive");
								httpget.setHeader(
										"Cookie",
										"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
								httpget.setHeader("Host", "news.open.qq.com");
								httpget.setHeader("Referer",
										"http://news.qq.com/");
								httpget.setHeader(
										"User-Agent",
										"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
								try {
									HttpResponse httpResponse = new DefaultHttpClient()
											.execute(httpget);
									if (httpResponse.getStatusLine()
											.getStatusCode() == 200) {
										String result = EntityUtils
												.toString(httpResponse
														.getEntity());
										String result_1 = result.substring(73);
										if (result_1.subSequence(0, 1).equals(
												":")) {
											json_result = result_1.substring(1,
													result_1.length() - 2);
										} else if (result_1.subSequence(0, 1)
												.equals("[")) {
											json_result = result_1.substring(0,
													result_1.length() - 2);
										} else {
											json_result = result_1.substring(2,
													result_1.length() - 2);
										}
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								List<Json> ps = gson.fromJson(json_result,
										new TypeToken<List<Json>>() {
										}.getType());
								for (int i = 0; i < ps.size(); i++) {
									Json ms = new Json();
									News ns = new News();
									ms = ps.get(i);

									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
									int year = t.year;
									int month = t.month + 1;
									int monthday = t.monthDay;
									int hour = t.hour;
									int minute = t.minute;
									final String title;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = ms.getLongtitle() + " (�Ƽ�)";
									image = ms.getImg();
									link = ms.getUrl();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = ms.getDesc();
									sort = "�Ƽ�";
									store = Environment
											.getExternalStorageDirectory()
											+ "/news_image/"
											+ "science/"
											+ title + ".jpeg";
									ns.setTitle(title);
									ns.setImage(image);
									ns.setLink(link);
									ns.setDesc(desc);
									ns.setDate(date);
									ns.setRefresh(refresh);
									ns.setSort(sort);
									ns.setStore(store);
									ns.setId(i);
									mList.add(ns);
								}

								DB db_insert = new DB(Main.this);
								db_insert.insert(mList);
								mList.clear();
								json_result = "";
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("�Ƽ�");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/science/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "science/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle
										// exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}
						}
					}).start();
					pager.setCurrentItem(3);
					science_state = true;
					tabid = 3;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
	
				if ("tab5".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("���");
					if (db_data.isEmpty() || db_data.size() < 10) {
						HttpURLConnection httpConn = null;
						new Thread(new Runnable() {
							@Override
							public void run() {
								HttpClient client = new DefaultHttpClient();
								// ʹ��Get��ʽ����
								HttpGet httpget = new HttpGet(
										"http://news.open.qq.com/cgi-bin/article.php?site=society&cnt=36&of=json&callback=jsonp1398774698563&_=1398775112723");
								// ����header��Ϣ
								httpget.setHeader("Accept", "*/*");
								httpget.setHeader("Accept-Encoding",
										"gzip,deflate,sdch");
								httpget.setHeader("Accept-Language",
										"zh-CN,zh;q=0.8");
								httpget.setHeader("Connection", "keep-alive");
								httpget.setHeader(
										"Cookie",
										"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
								httpget.setHeader("Host", "news.open.qq.com");
								httpget.setHeader("Referer",
										"http://news.qq.com/");
								httpget.setHeader(
										"User-Agent",
										"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
								try {
									HttpResponse httpResponse = new DefaultHttpClient()
											.execute(httpget);
									if (httpResponse.getStatusLine()
											.getStatusCode() == 200) {
										String result = EntityUtils
												.toString(httpResponse
														.getEntity());
										String result_1 = result.substring(73);
										if (result_1.subSequence(0, 1).equals(
												":")) {
											json_result = result_1.substring(1,
													result_1.length() - 2);
										} else if (result_1.subSequence(0, 1)
												.equals("[")) {
											json_result = result_1.substring(0,
													result_1.length() - 2);
										} else {
											json_result = result_1.substring(2,
													result_1.length() - 2);
										}
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								List<Json> ps = gson.fromJson(json_result,
										new TypeToken<List<Json>>() {
										}.getType());
								System.out.println("���ps:" + ps.size());
								for (int i = 0; i < ps.size(); i++) {
									Json ms = new Json();
									News ns = new News();
									ms = ps.get(i);

									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
									int year = t.year;
									int month = t.month + 1;
									int monthday = t.monthDay;
									int hour = t.hour;
									int minute = t.minute;
									final String title;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = ms.getLongtitle() + " (���)";
									image = ms.getImg();
									link = ms.getUrl();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = ms.getDesc();
									sort = "���";
									store = Environment
											.getExternalStorageDirectory()
											+ "/news_image/"
											+ "social/"
											+ title + ".jpeg";
									ns.setTitle(title);
									ns.setImage(image);
									System.out.println("���" + image);
									ns.setLink(link);
									ns.setDesc(desc);
									ns.setDate(date);
									ns.setRefresh(refresh);
									ns.setSort(sort);
									ns.setStore(store);
									ns.setId(i);
									mList.add(ns);
								}

								DB db_insert = new DB(Main.this);
								db_insert.insert(mList);
								mList.clear();
								json_result = "";
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("���");
					new Thread(new Runnable() {
						@Override
						public void run() {

							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								System.out.println("social" + image);
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/social/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "social/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle
										// exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}
						}
					}).start();
					pager.setCurrentItem(4);
					social_state = true;
					tabid = 4;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab6".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("�ƾ�");
					if (db_data.isEmpty() || db_data.size() < 10) {
						HttpURLConnection httpConn = null;
						new Thread(new Runnable() {
							@Override
							public void run() {
								HttpClient client = new DefaultHttpClient();
								// ʹ��Get��ʽ����
								HttpGet httpget = new HttpGet(
										"http://news.open.qq.com/cgi-bin/article.php?site=finance&cnt=36&of=json&callback=jsonp1401022281105&_=1401026243470");
								// ����header��Ϣ
								httpget.setHeader("Accept", "*/*");
								httpget.setHeader("Accept-Encoding",
										"gzip,deflate,sdch");
								httpget.setHeader("Accept-Language",
										"zh-CN,zh;q=0.8");
								httpget.setHeader("Connection", "keep-alive");
								httpget.setHeader(
										"Cookie",
										"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
								httpget.setHeader("Host", "news.open.qq.com");
								httpget.setHeader("Referer",
										"http://news.qq.com/");
								httpget.setHeader(
										"User-Agent",
										"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
								try {
									HttpResponse httpResponse = new DefaultHttpClient()
											.execute(httpget);
									if (httpResponse.getStatusLine()
											.getStatusCode() == 200) {
										String result = EntityUtils
												.toString(httpResponse
														.getEntity());
										String result_1 = result.substring(73);
										if (result_1.subSequence(0, 1).equals(
												":")) {
											json_result = result_1.substring(1,
													result_1.length() - 2);
										} else if (result_1.subSequence(0, 1)
												.equals("[")) {
											json_result = result_1.substring(0,
													result_1.length() - 2);
										} else {
											json_result = result_1.substring(2,
													result_1.length() - 2);
										}
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								List<Json> ps = gson.fromJson(json_result,
										new TypeToken<List<Json>>() {
										}.getType());
								for (int i = 0; i < ps.size(); i++) {
									Json ms = new Json();
									News ns = new News();
									ms = ps.get(i);

									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
									int year = t.year;
									int month = t.month + 1;
									int monthday = t.monthDay;
									int hour = t.hour;
									int minute = t.minute;
									final String title;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = ms.getLongtitle() + " (�ƾ�)";
									image = ms.getImg();
									link = ms.getUrl();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = ms.getDesc();
									sort = "�ƾ�";
									store = Environment
											.getExternalStorageDirectory()
											+ "/news_image/"
											+ "finance/"
											+ title + ".jpeg";
									ns.setTitle(title);
									ns.setImage(image);
									ns.setLink(link);
									ns.setDesc(desc);
									ns.setDate(date);
									ns.setRefresh(refresh);
									ns.setSort(sort);
									ns.setStore(store);
									ns.setId(i);
									mList.add(ns);
								}

								DB db_insert = new DB(Main.this);
								db_insert.insert(mList);
								mList.clear();
								json_result = "";
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("�ƾ�");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/finance/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "finance/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle
										// exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}
						}
					}).start();
					pager.setCurrentItem(5);
					finance_state = true;
					tabid = 5;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab7".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						HttpURLConnection httpConn = null;
						new Thread(new Runnable() {
							@Override
							public void run() {
								HttpClient client = new DefaultHttpClient();
								// ʹ��Get��ʽ����
								HttpGet httpget = new HttpGet(
										"http://news.open.qq.com/cgi-bin/article.php?site=ent&cnt=36&of=json&callback=jsonp1401022281090&_=1401022819033");
								// ����header��Ϣ
								httpget.setHeader("Accept", "*/*");
								httpget.setHeader("Accept-Encoding",
										"gzip,deflate,sdch");
								httpget.setHeader("Accept-Language",
										"zh-CN,zh;q=0.8");
								httpget.setHeader("Connection", "keep-alive");
								httpget.setHeader(
										"Cookie",
										"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
								httpget.setHeader("Host", "news.open.qq.com");
								httpget.setHeader("Referer",
										"http://news.qq.com/");
								httpget.setHeader(
										"User-Agent",
										"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
								try {
									HttpResponse httpResponse = new DefaultHttpClient()
											.execute(httpget);
									if (httpResponse.getStatusLine()
											.getStatusCode() == 200) {
										String result = EntityUtils
												.toString(httpResponse
														.getEntity());
										String result_1 = result.substring(73);
										if (result_1.subSequence(0, 1).equals(
												":")) {
											json_result = result_1.substring(1,
													result_1.length() - 2);
										} else if (result_1.subSequence(0, 1)
												.equals("[")) {
											json_result = result_1.substring(0,
													result_1.length() - 2);
										} else {
											json_result = result_1.substring(2,
													result_1.length() - 2);
										}
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								List<Json> ps = gson.fromJson(json_result,
										new TypeToken<List<Json>>() {
										}.getType());
								for (int i = 0; i < ps.size(); i++) {
									Json ms = new Json();
									News ns = new News();
									ms = ps.get(i);

									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
									int year = t.year;
									int month = t.month + 1;
									int monthday = t.monthDay;
									int hour = t.hour;
									int minute = t.minute;
									final String title;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = ms.getLongtitle() + " (����)";
									image = ms.getImg();
									link = ms.getUrl();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = ms.getDesc();
									sort = "����";
									store = Environment
											.getExternalStorageDirectory()
											+ "/news_image/"
											+ "joy/"
											+ title + ".jpeg";
									ns.setTitle(title);
									ns.setImage(image);
									ns.setLink(link);
									ns.setDesc(desc);
									ns.setDate(date);
									ns.setRefresh(refresh);
									ns.setSort(sort);
									ns.setStore(store);
									ns.setId(i);
									mList.add(ns);
								}

								DB db_insert = new DB(Main.this);
								db_insert.insert(mList);
								mList.clear();
								json_result = "";
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {

							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/joy/"
										+ title + ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "joy/",
												title + ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle
										// exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}
						}
					}).start();
					pager.setCurrentItem(6);
					joy_state = true;
					tabid = 6;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}

				if ("tab8".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						HttpURLConnection httpConn = null;
						new Thread(new Runnable() {
							@Override
							public void run() {
								HttpClient client = new DefaultHttpClient();
								// ʹ��Get��ʽ����
								HttpGet httpget = new HttpGet(
										"http://news.open.qq.com/cgi-bin/article.php?site=sports&cnt=36&of=json&callback=jsonp1398130111699&_=1398130992850");
								// ����header��Ϣ
								httpget.setHeader("Accept", "*/*");
								httpget.setHeader("Accept-Encoding",
										"gzip,deflate,sdch");
								httpget.setHeader("Accept-Language",
										"zh-CN,zh;q=0.8");
								httpget.setHeader("Connection", "keep-alive");
								httpget.setHeader(
										"Cookie",
										"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
								httpget.setHeader("Host", "news.open.qq.com");
								httpget.setHeader("Referer",
										"http://news.qq.com/");
								httpget.setHeader(
										"User-Agent",
										"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
								try {
									HttpResponse httpResponse = new DefaultHttpClient()
											.execute(httpget);
									if (httpResponse.getStatusLine()
											.getStatusCode() == 200) {
										String result = EntityUtils
												.toString(httpResponse
														.getEntity());
										String result_1 = result.substring(73);

										if (result_1.subSequence(0, 1).equals(
												":")) {
											json_result = result_1.substring(1,
													result_1.length() - 2);
										} else if (result_1.subSequence(0, 1)
												.equals("[")) {
											json_result = result_1.substring(0,
													result_1.length() - 2);
										} else {
											json_result = result_1.substring(2,
													result_1.length() - 2);
										}
										System.out.println("����" + json_result);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								List<Json> ps = gson.fromJson(json_result,
										new TypeToken<List<Json>>() {
										}.getType());
								for (int i = 0; i < ps.size(); i++) {
									Json ms = new Json();
									News ns = new News();
									ms = ps.get(i);

									Time t = new Time();
									t.setToNow(); // ȡ��ϵͳʱ�䡣
									int year = t.year;
									int month = t.month + 1;
									int monthday = t.monthDay;
									int hour = t.hour;
									int minute = t.minute;
									final String title;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = ms.getLongtitle() + " (����)";
									image = ms.getImg();
									link = ms.getUrl();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = ms.getDesc();
									sort = "����";
									store = Environment
											.getExternalStorageDirectory()
											+ "/news_image/"
											+ "sports/"
											+ title + ".jpeg";
									ns.setTitle(title);
									ns.setImage(image);
									ns.setLink(link);
									ns.setDesc(desc);
									ns.setDate(date);
									ns.setRefresh(refresh);
									ns.setSort(sort);
									ns.setStore(store);
									ns.setId(i);
									mList.add(ns);
								}

								DB db_insert = new DB(Main.this);
								db_insert.insert(mList);
								mList.clear();
								json_result = "";
							}
						}).start();
					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/sports/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "sports/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle
										// exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}
					}).start();
					pager.setCurrentItem(7);
					sports_state = true;
					tabid = 7;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				
				
	//**********************************************************
			
				if ("tab9".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25990").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");									
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
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
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(����)";				
										System.out.println(title+"#####################################################");
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																	
										image =div_2.get(i).select(".tiptitleImg img").attr("src");										
										desc = div_1.get(i).getElementsByTag("p")
												.text();												
										date = year + "-" + month + "-"
												+ monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;									
										desc = div_1.get(i)
												.getElementsByTag("p").text();
										sort = "����";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "pengpai/"
												+ title + ".jpeg";
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
									System.out.println("0111000000000");
									System.out.println(mList.isEmpty()+"111111111110000000000000000000000");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/pengpai/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "pengpai/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(8);
					pengpai_state = true;
					tabid = 8;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}		
				
				if ("tab10".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25427").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");									
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
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
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(����)";										
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																	
										image =div_2.get(i).select(".tiptitleImg img").attr("src");										
										desc = div_1.get(i).getElementsByTag("p")
												.text();												
										date = year + "-" + month + "-"
												+ monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;									
										desc = div_1.get(i)
												.getElementsByTag("p").text();
										sort = "����";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "people/"
												+ title + ".jpeg";
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
									System.out.println("0111000000000");
									System.out.println(mList.isEmpty()+"111111111110000000000000000000000");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/people/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "people/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(9);
					people_state = true;
					tabid = 9;
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}		
				
				if ("tab11".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25842").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(����)";									
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																
										image =div_2.get(i).select(".tiptitleImg img").attr("src");									
										desc = div_1.get(i).getElementsByTag("p")
												.text();
										date = year + "-" + month + "-" + monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										sort = "����";									
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "travel/"
												+ title + ".jpeg";
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
									//System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/travel/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "travel/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(10);
					travel_state = true;
					tabid = 10;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
						
				if ("tab12".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("��Ϸ");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25448").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(��Ϸ)";									
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																
										image =div_2.get(i).select(".tiptitleImg img").attr("src");									
										desc = div_1.get(i).getElementsByTag("p")
												.text();
										date = year + "-" + month + "-" + monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										sort = "��Ϸ";							
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "movie/"
												+ title + ".jpeg";
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
									System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("��Ϸ");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/movie/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "movie/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(11);
					movie_state = true;
					tabid = 11;
					System.out.println("��Ϸ");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				
				
				if ("tab13".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_26202").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(����)";									
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																
										image =div_2.get(i).select(".tiptitleImg img").attr("src");									
										desc = div_1.get(i).getElementsByTag("p")
												.text();
										date = year + "-" + month + "-" + monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										sort = "����";									
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "parents/"
												+ title + ".jpeg";
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
									System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/parents/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "parents/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(12);
					parents_state = true;
					tabid = 12;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab14".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25769").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ "(����)";									
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");																
										image =div_2.get(i).select(".tiptitleImg img").attr("src");									
										desc = div_1.get(i).getElementsByTag("p")
												.text();
										date = year + "-" + month + "-" + monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										sort = "����";								
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "life/"
												+ title + ".jpeg";
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
									System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/life/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "life/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(13);
					life_state = true;
					tabid = 13;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}
				if ("tab15".equals(tabId)) {
					List<News> db_data = new ArrayList<News>();
					DB news_db = new DB(Main.this);
					db_data = news_db.check("����");
					if (db_data.isEmpty() || db_data.size() < 10) {
						new Thread(new Runnable() {
							@TargetApi(Build.VERSION_CODES.GINGERBREAD)
							@Override
							public void run() {
								try {
									Document doc = Jsoup.connect(
											"http://www.thepaper.cn/list_25942").get();
									Elements div_1 = doc.select(".news_li");
									Elements div_2 = doc.select(".news_tu");
									for (int i = 0; i < div_1.size(); i++) {
										News ns = new News();
										Time t = new Time();
										t.setToNow(); // ȡ��ϵͳʱ�䡣
										int year = t.year;
										int month = t.month + 1;
										int monthday = t.monthDay;
										int hour = t.hour;
										int minute = t.minute;
										final String title;
										String desc;
										final String image;
										String link, date, sort, refresh, store;
										title = div_1.get(i).getElementsByTag("h2")
												.text()+ " (����)";
										//System.out.println(title+"title********����****************************");
										link = "http://www.thepaper.cn/"
												+ div_2.get(i).select(".tiptitleImg").attr("href");							
										//System.out.println(link+"link*****����*******************************");
										image =div_2.get(i).select(".tiptitleImg img").attr("src");
										//System.out.println(image+"image*******����*****************************");
										desc = div_1.get(i).getElementsByTag("p")
												.text();
										date = year + "-" + month + "-" + monthday;
										refresh = year + "-" + month + "-"
												+ monthday + " " + hour + ":"
												+ minute;
										sort = "����";
										store = Environment
												.getExternalStorageDirectory()
												+ "/news_image/"
												+ "body/"
												+ title + ".jpeg";
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
									//System.out.println(mList.isEmpty()+"11111");
									DB db_insert = new DB(Main.this);
									db_insert.insert(mList);
									mList.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();

					}
					mList.clear();
					DB news_check = new DB(Main.this);
					mList = news_check.check("����");
					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// ������ͼƬ����ڱ���
								if (!tool.isExist("news_image/body/" + title
										+ ".jpeg")) {
									try {
										URL url = new URL(image);
										InputStream is = url.openStream();
										bitmap = BitmapFactory.decodeStream(is);
										SaveImage save = new SaveImage();
										bitmap = save.setImage(bitmap);
										save.saveFile(
												bitmap,
												Environment
														.getExternalStorageDirectory()
														+ "/news_image/"
														+ "body/", title
														+ ".jpeg");
										is.close();
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�쳣"
												+ e.getMessage());
									}
								}
							}

						}

					}).start();
					pager.setCurrentItem(14);
					body_state = true;
					tabid = 14;
					System.out.println("����");
					tv = (TextView) tabWidget.getChildAt(tabid).findViewById(
							android.R.id.title);
					tv.setTextSize(18);
					tv.setTextColor(getResources().getColor(R.color.red));
					tv.setGravity(Gravity.CENTER);
					TextPaint tp = tv.getPaint();
					tp.setFakeBoldText(true);
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						if (i != tabid) {
							tv = (TextView) tabWidget.getChildAt(i)
									.findViewById(android.R.id.title);
							tv.setTextSize(18);
							tv.setGravity(Gravity.CENTER);
							TextPaint tp_title = tv.getPaint();
							tp_title.setFakeBoldText(true);
							tv.setTextColor(getResources().getColor(
									R.color.BLACK2));
						}
					}
				}	
				
	//************************************************************

			}
		});

		
		pager.setAdapter(new MyPageAdapter(listViews));
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			int first_move = -1;

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg) {
				// TODO Auto-generated method stub
				tabHost.setCurrentTab(arg);
				HorizontalScrollView h = (HorizontalScrollView) Main.this
						.findViewById(R.id.h_scrollFoodType);
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int move = dm.widthPixels / 5;
				System.out.println(dm.widthPixels);
				if (first_move < arg) {
					h.smoothScrollTo(h.getScrollX() + move, 0);// ����
				}
				if (first_move > arg) {
					h.smoothScrollTo(h.getScrollX() - move, 0);// ����
				}
				first_move = arg;
			}
		});
           //////////////////////////////////////////////////////
          //��ʼ��picture_open ����Ӽ����¼�
          picture_open = (ImageButton) this.findViewById(R.id.pic_open);
          picture_open.setOnClickListener(new OnClickListener(){

          @Override
          public void onClick(View arg0) {
          // TODO Auto-generated method stub
          Intent p = new Intent();
          p.setClass(Main.this, Picture.class);
         Main.this.startActivity(p);
         }
       });
		
		//////////////////////////////////////////////////////
		//��ʼ��video_open ����Ӽ����¼�
		video_open = (ImageButton) this.findViewById(R.id.vid_open);
		video_open.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent t = new Intent();
				t.setClass(Main.this, Video.class);
				Main.this.startActivity(t);
			}});
		
		//////////////////////////////////////////////////////
		
		voice_open = (ImageButton) findViewById(R.id.voice_open);
		voice_open.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mSpeech.stop();
				mList.clear();
				content = "";
				switch (tabid) {
				case 0:
					System.out.println(0);
					mList = Yaowen.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 1:
					mList = Global.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					System.out.println(content);
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 2:
					mList = China.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					System.out.println(content);
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 3:
					System.out.println(3);
					mList = Science.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					System.out.println(content);
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 4:
					System.out.println(4);
					mList = Social.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 5:
					System.out.println(5);
					mList = Finance.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 6:
					System.out.println(6);
					mList = Joy.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 7:
					System.out.println(7);
					mList = Sports.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 8:
					System.out.println(8);
					mList = Pengpai.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 9:
					System.out.println(9);
					mList = People.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 10:
					System.out.println(10);
					mList = Travel.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 11:
					System.out.println(11);
					mList = Movie.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 12:
					System.out.println(12);
					mList = Parents.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 13:
					System.out.println(13);
					mList = Life.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				case 14:
					System.out.println(14);
					mList = Body.mList;
					for (int i = 0; i < mList.size(); i++) {
						News ns = mList.get(i);
						if (i <= mList.size() - 2) {
							content += ns.getTitle() + "��һ��" + "\n\r";
						} else {
							content += ns.getTitle();
						}
					}
					mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
					break;
				default:
					break;
				}
			}
		});
		main_sys = (ImageButton) findViewById(R.id.main_set_btn);
		main_sys.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				View contentView = getLayoutInflater().inflate(
						R.layout.main_pop, null);
				// ��ʼ���ؼ�

		/*		main_voice_speed = (SeekBar) contentView
						.findViewById(R.id.main_voice_speed);
				main_voice_speed.setProgress(30);
				main_voice_speed
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							float speed;

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// TODO Auto-generated method stub
								speed = (float) progress * 3 / 90;
								System.out.println(speed);

							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
								mSpeech.setSpeechRate(speed);
								mSpeech.speak(content,
										TextToSpeech.QUEUE_FLUSH, null);
							}

						});
		//wqq  ע��end	*/			
		
				main_light_value = (SeekBar) contentView
						.findViewById(R.id.main_luminance_value);
				main_light_value.setProgress(100);
				main_light_value
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// TODO Auto-generated method stub
								WindowManager.LayoutParams params = Main.this
										.getWindow().getAttributes();
								params.screenBrightness = progress / 255f;
								Main.this.getWindow().setAttributes(
										params);
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub

							}

						});
		/*		
//*********************************************************************************
				List<String> list_text1 = new ArrayList<String>();
				list_text1.add("14sp");
				list_text1.add("16sp");
				list_text1.add("18sp");
				list_text1.add("20sp");
				list_text1.add("22sp");
				list_text1.add("24sp");
				list_text1.add("26sp");
				list_text1.add("28sp");
				list_text1.add("30sp");
				maintext_size = (Spinner) contentView
						.findViewById(R.id.main_text_size);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item, list_text1);
				adapter.setDropDownViewResource(R.layout.spinner_item);
				maintext_size.setAdapter(adapter);
				maintext_size
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								TextView tv = (TextView) arg1;
								tv.setGravity(Gravity.CENTER);
								tv.setTextColor(getResources().getColor(
										R.color.red));
									
								// mContent1.setTextSize(arg2 * 2 + 14);
								
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								
								// TODO Auto-generated method stub
								mContent1.setTextSize(16);
							}

						});
		
				List<String> list_color1 = new ArrayList<String>();				
				list_color1.add("��ɫ");
				list_color1.add("�ۺ�ɫ");
				list_color1.add("��ɫ");
				list_color1.add("�Ϻ�ɫ");
				list_color1.add("����ɫ");
				list_color1.add("��ɫ");
				list_color1.add("�ɿ���ɫ");				
				list_color1.add("��ɫ");
				list_color1.add("��ɫ");
				maintext_color = (Spinner) contentView
						.findViewById(R.id.main_text_color);
				ArrayAdapter<String> adapter_color = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item,
						list_color1);
				adapter_color.setDropDownViewResource(R.layout.spinner_item);
				maintext_color.setAdapter(adapter_color);
				
				maintext_color
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								TextView tv = (TextView) arg1;
								tv.setGravity(Gravity.CENTER);
								tv.setTextColor(getResources().getColor(
										R.color.red));
								
								switch (arg2) {
								case 0:
									mContent1.setTextColor(getResources().getColor(
											R.color.black));
									break;
								case 1:
									mContent1.setTextColor(getResources().getColor(
											R.color.lightpink));
									break;
								case 2:
									mContent1.setTextColor(getResources().getColor(
											R.color.pink));
									break;
								case 3:
									mContent1.setTextColor(getResources().getColor(
											R.color.magenta));
									break;
								case 4:
									mContent1.setTextColor(getResources().getColor(
											R.color.oldlace));
									break;
								case 5:
									mContent1.setTextColor(getResources().getColor(
											R.color.peru));
									break;
								case 6:
									mContent1.setTextColor(getResources().getColor(
											R.color.skyblue));
									break;
								case 7:
									mContent1.setTextColor(getResources().getColor(
											R.color.maroon));
									break;
								case 8:
									mContent1.setTextColor(getResources().getColor(
											R.color.darkgreen));
									break;
								default:
									break;
								}

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}

						});   */
				
//************************************************************************************
				
		
			/*	main_stop_btn = (CheckBox) contentView
						.findViewById(R.id.main_voice_stop);
				main_stop_btn
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									mSpeech.stop();
								} else {
									mSpeech.speak(content,
											TextToSpeech.QUEUE_FLUSH, null);
								}
							}

						}); */
				List<String> list = new ArrayList<String>();
				list.add("ȫ��");
				list.add("Ҫ��");
				list.add("����");
				list.add("����");	
				list.add("�Ƽ�");	
				list.add("���");
				list.add("�ƾ�");
				list.add("����");
				list.add("����");
				list.add("����");
				list.add("����");
				list.add("����");
				list.add("��Ϸ");				
				list.add("����");
				list.add("����");
				list.add("����");

				main_news_sort = (Spinner) contentView
						.findViewById(R.id.news_sort);
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item, list);
				adapter1.setDropDownViewResource(R.layout.spinner_item);
				main_news_sort.setAdapter(adapter1);
				main_news_sort
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								TextView tv = (TextView) arg1;
								tv.setGravity(Gravity.CENTER);
								tv.setTextColor(getResources().getColor(
										R.color.skyblue));
								switch (arg2) {
								case 0:
									delete_sort = "ȫ��";
									break;
								case 1:
									delete_sort = "Ҫ��";
									break;
								case 2:
									delete_sort = "����";
									break;
								case 3:
									delete_sort = "����";
									break;								
								case 4:
									delete_sort = "�Ƽ�";
									break;
								case 5:
									delete_sort = "���";
									break;
								case 6:
									delete_sort = "�ƾ�";
									break;
								case 7:
									delete_sort = "����";
									break;
								case 8:
									delete_sort = "����";
									break;
								case 9:
									delete_sort = "����";
									break;
								case 10:
									delete_sort = "����";
									break;
								case 11:
									delete_sort = "����";
									break;
								case 12:
									delete_sort = "��Ϸ";
									break;
								case 13:
									delete_sort = "����";
									break;
								case 14:
									delete_sort = "����";
									break;
								case 15:
									delete_sort = "����";
									break;
								default:
									break;
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}

						});
				main_out = (Button) contentView.findViewById(R.id.sys_out);
				main_out.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(
								Main.this);
						builder.setMessage("�Ƿ�Ҫ�˳��������ģ�")
								.setPositiveButton("��",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												finish();
												System.exit(0);
											}
										}).setNegativeButton("��", null);
						AlertDialog ad = builder.create();
						ad.show();
					}
				});
				main_delete = (Button) contentView
						.findViewById(R.id.news_delete);
				main_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(
								Main.this);
						builder.setMessage("�Ƿ�Ҫɾ�����ݣ�")
								.setPositiveButton("��",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												DB db_insert = new DB(
														Main.this);
												db_insert.delete(delete_sort);
												if (delete_sort.equals("ȫ��")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("Ҫ��")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "yaowen/");
													tool.deleteFile(file);
												}
												
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "global/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "china/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("�Ƽ�")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "science/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("���")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "social/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("�ƾ�")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "finance/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "joy/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "sports/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "pengpai/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "people/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "travel/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("��Ϸ")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "movie/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "parents/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "life/");
													tool.deleteFile(file);
												}
												if (delete_sort.equals("����")) {
													File file = new File(
															Environment
																	.getExternalStorageDirectory()
																	+ "/news_image/"
																	+ "body/");
													tool.deleteFile(file);
												}
												Message msg = new Message();
												msg.obj = delete_sort
														+ "��������ɾ��";
												handler.sendMessage(msg);
											}
										}).setNegativeButton("��", null);
						AlertDialog ad = builder.create();
						ad.show();
					}
				});

				popupWindow = new PopupWindow(contentView,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						true);
				parent = getLayoutInflater().inflate(R.layout.main,
						null);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.setAnimationStyle(R.style.PopupAnimation);
				popupWindow.showAtLocation(parent, Gravity.CENTER
						| Gravity.CENTER, 0, 0);

				popupWindow.update();
			}
		});

	}

	private View getView(String id, Intent intent) {
		// TODO Auto-generated method stub
		return manager.startActivity(id, intent).getDecorView();
	}

	class MyPageAdapter extends PagerAdapter {
		private List<View> list;

		private MyPageAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(list.get(position));
			return list.get(position);
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
			((ViewPager) container).removeView(list.get(position));
		}

		// public CharSequence getPageTitle(int position) {
		// // TODO Auto-generated method stub
		// return list.get(position);
		// }

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Message msg = new Message();
				msg.obj = "�ٰ�һ�ν��˳���������";
				handler.sendMessage(msg);
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
				isNetworkAvailable = false;
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// ��Activity��ʾ������ÿ�������л�һ��ͼƬ��ʾ
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// ��Activity���ɼ���ʱ��ֹͣ�л�
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * �����л�����
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				// System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler_nav.obtainMessage().sendToTarget(); // ͨ��Handler�л�ͼƬ
			}
		}
	}

	/**
	 * ��ViewPager��ҳ���״̬�����ı�ʱ����
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * ���ViewPagerҳ���������
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 5;
		}
		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			
			View view = imageViews.get(arg1);
			
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stu
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (isNetworkAvailable) {
								Bundle Data = new Bundle();
								Data.putString("news_link", nav_link.get(arg1));
								Data.putString("news_sort", "ͷ��");//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=S
								Intent intent = new Intent(Main.this,
										Webview.class);
								intent.putExtras(Data);
								startActivity(intent);
							} else {
								Message msg = new Message();
								msg.obj = "��������û����,�޷�ִ����Ӧ����!";
								handler.sendMessage(msg);
							}
						}
					}).start();
			
				}
			});
			return imageViews.get(arg1);
		}
		
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

}
