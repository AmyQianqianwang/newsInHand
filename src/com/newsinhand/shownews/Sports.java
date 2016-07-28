package com.newsinhand.shownews;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.newsinhand.database.DB;
import com.newsinhand.definition.Json;
import com.newsinhand.definition.News;
import com.newsinhand.fileoperate.FileOper;
import com.newsinhand.fileoperate.SaveImage;
import com.newsinhand.refreshnews.Refresh;
import com.newsinhand.refreshnews.Refresh.OnLoadMoreListener;
import com.newsinhand.refreshnews.Refresh.OnRefreshListener;
import com.example.newsinhand.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Sports extends Activity {
	private static final String TAG = "Sports";
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/news_image/" + "sports/";
	Bitmap bitmap;
	FileOper tool = new FileOper();
	static String json_result;
	public static List<News> mList = new ArrayList<News>();
	static boolean state = true;
	private Refresh mListView;
	private int mCount = 10;
	private ArrayList<HashMap<String, Object>> listItem;
	ImageView imageview;
	int index = 0;
	@SuppressWarnings("unchecked")
	private Handler mHandler_initiate = new Handler() {
		public void handleMessage(Message msg) {
			List<News> news = (ArrayList<News>) msg.obj;
			for (int i = 0; i < 10; i++) {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				final News ns = (News) news.get(i);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(ns.getStore(), options);
				if (bitmap == null) {
					map.put("ItemImage", R.drawable.yaowen_noon);
				} else {
					map.put("ItemImage", bitmap);
				}
				map.put("ItemTitle", ns.getTitle());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);

			}
			// Toast.makeText(XinwenActivity.this,String.valueOf(jsonArray.length()),
			// Toast.LENGTH_LONG).show();
			// 生成适配器的Item和动态数组对应的元素
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Sports.this, listItem,// 数据源
					R.layout.sports_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.sports_image, R.id.sports_title,
							R.id.sports_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// 添加并且显示
			mListView.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
			index = 10;
		}
	};
	private Handler mHandler_refresh = new Handler() {

		public void handleMessage(Message msg) {
			List<News> news = (ArrayList<News>) msg.obj;
			listItem.removeAll(listItem);
			for (int i = 0; i < 10; i++) {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				final News ns = (News) news.get(i);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(ns.getStore(), options);
				if (bitmap == null) {
					map.put("ItemImage", R.drawable.yaowen_noon);
				} else {
					map.put("ItemImage", bitmap);
				}
				map.put("ItemTitle", ns.getTitle());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Sports.this, listItem,// 数据源
					R.layout.sports_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.sports_image, R.id.sports_title,
							R.id.sports_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// 添加并且显示
			mListView.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
			index = 10;
			System.out.println("下拉刷新");
			mListView.onRefreshComplete(); // 下拉刷新完成
			System.out.println("下拉刷新完成");
		}
	};

	private Handler mHandler_loader = new Handler() {

		public void handleMessage(Message msg) {
			List<News> news = (ArrayList<News>) msg.obj;
			int length = 0;
			if (10 + index > news.size()) {
				length = news.size();
			} else {
				length = 10 + index;
			}
			for (int i = index; i < length; i++) {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				final News ns = (News) news.get(i);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(ns.getStore(), options);
				if (bitmap == null) {
					map.put("ItemImage", R.drawable.yaowen_noon);
				} else {
					map.put("ItemImage", bitmap);
				}
				map.put("ItemTitle", ns.getTitle());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Sports.this, listItem,// 数据源
					R.layout.sports_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.sports_image, R.id.sports_title,
							R.id.sports_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// 添加并且显示
			mListView.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
			mListView.onLoadMoreComplete(index); // 加载更多完成
			index = length;
		}

	};
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			Toast.makeText(Sports.this, obj, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		setContentView(R.layout.sports);

		mListView = (Refresh) findViewById(R.id.sports_list);
		listItem = new ArrayList<HashMap<String, Object>>();
		List<News> db_data = new ArrayList<News>();
		DB news_db = new DB(Sports.this);
		db_data = news_db.check("体育");
		if (db_data.isEmpty() || db_data.size() < 10) {
			HttpURLConnection httpConn = null;
			if (Yaowen.First_show) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						HttpClient client = new DefaultHttpClient();
						// 使用Get方式请求
						HttpGet httpget = new HttpGet(
								"http://news.open.qq.com/cgi-bin/article.php?site=sports&cnt=36&of=json&callback=jsonp1398130111699&_=1398130992850");
						// 设置header消息
						httpget.setHeader("Accept", "*/*");
						httpget.setHeader("Accept-Encoding",
								"gzip,deflate,sdch");
						httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
						httpget.setHeader("Connection", "keep-alive");
						httpget.setHeader(
								"Cookie",
								"pgv_pvi=1216941056; isVideo_DC=0; ptui_loginuin=125830593; pt2gguin=o0125830593; RK=B2lSx+SB2G; ptcz=c3310ee32bc99893e32c169e38ef418067acb1b08284e49f0d8467eb51923e8d; pgv_info=ssid=s1083040928; pgv_pvid=9794937247; o_cookie=125830593");
						httpget.setHeader("Host", "news.open.qq.com");
						httpget.setHeader("Referer", "http://news.qq.com/");
						httpget.setHeader(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
						try {
							HttpResponse httpResponse = new DefaultHttpClient()
									.execute(httpget);
							if (httpResponse.getStatusLine().getStatusCode() == 200) {
								String result = EntityUtils
										.toString(httpResponse.getEntity());
								String result_1 = result.substring(73);
								if (result_1.subSequence(0, 1).equals(":")) {
									json_result = result_1.substring(1,
											result_1.length() - 2);
								} else if (result_1.subSequence(0, 1).equals(
										"[")) {
									json_result = result_1.substring(0,
											result_1.length() - 2);
								} else {
									json_result = result_1.substring(2,
											result_1.length() - 2);
								}
								System.out.println("体育" + json_result);
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
							t.setToNow(); // 取得系统时间。
							int year = t.year;
							int month = t.month + 1;
							int monthday = t.monthDay;
							int hour = t.hour;
							int minute = t.minute;
							final String title;
							String desc;
							final String image;
							String link, date, sort, refresh, store;
							title = ms.getLongtitle() + " (体育)";
							image = ms.getImg();
							link = ms.getUrl();
							date = year + "-" + month + "-" + monthday;
							refresh = year + "-" + month + "-" + monthday + " "
									+ hour + ":" + minute;
							desc = ms.getDesc();
							sort = "体育";
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
							mList.add(ns);
						}

						DB db_insert = new DB(Sports.this);
						db_insert.insert(mList);
						mList.clear();
						DB news_check = new DB(Sports.this);
						mList = news_check.check("体育");
					}
				}).start();
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (state) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (Main.social_state == true) {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// 将网络图片存放在本地
								if (!tool.isExist("news_image/sports/" + title
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
										System.out.println("异常"
												+ e.getMessage());
									}
								}
							}
							Message msg = new Message();
							msg.obj = mList;
							mHandler_initiate.sendMessage(msg);
							Main.sports_state = false;
							state = false;
						}
					}
				}
			}).start();
		} else {
			mList = db_data;
			Message msg = new Message();
			msg.obj = mList;
			mHandler_initiate.sendMessage(msg);
		}
		initView();
	}

	private void initView() {
		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				// TODO 下拉刷新
				if (com.newsinhand.shownews.Main.isNetworkAvailable == true)  {
					Log.e(TAG, "onRefresh");
					loadData(0);
				} else {
					refresh_over();
				}

			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			public void onLoadMore() {
				// TODO 加载更多
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int index = arg2 - 2;
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (Main.isNetworkAvailable) {
							HashMap<String, Object> map = listItem.get(index);
							String title = (String) map.get("ItemTitle");
							String abstruction = (String) map.get("ItemText");
							String link = (String) map.get("ItemLink");
							Bundle Data = new Bundle();
							Data.putString("news_link", link);
							Data.putString("news_sort", "体育");
							Intent intent = new Intent(Sports.this,
									Webview.class);
							intent.putExtras(Data);
							startActivity(intent);
						} else {
							Message msg = new Message();
							msg.obj = "网络差或者没连接,无法执行相应操作!";
							handler.sendMessage(msg);
						}
					}
				}).start();
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent,
					final View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map = listItem.get(position - 2);
				final String title = (String) map.get("ItemTitle");
				String abstruction = (String) map.get("ItemText");
				final String link = (String) map.get("ItemLink");
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Sports.this);
				builder.setMessage("是否语音播报新闻？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										System.out.println(title);
										Bundle Data = new Bundle();
										Data.putString("news_title", title);
										Data.putString("news_link", link);
										Data.putString("news_sort", "体育");
										Intent intent = new Intent(
												Sports.this,
												TTS.class);
										intent.putExtras(Data);
										startActivity(intent);

									}
								}).setNegativeButton("否", null);
				AlertDialog ad = builder.create();
				ad.show();
				return true;
			}
		});
	}

	public void refresh_over() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.obj = mList;
				mHandler_refresh.sendMessage(msg);

				Message msg_show = new Message();
				msg_show.obj = "无网络连接，刷新失败!";
				handler.sendMessage(msg_show);
			}
		}.start();
	}

	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					mList.clear();
					json_result = "";
					new Thread(new Runnable() {
						@Override
						public void run() {
							HttpClient client = new DefaultHttpClient();
							// 使用Get方式请求
							HttpGet httpget = new HttpGet(
									"http://news.open.qq.com/cgi-bin/article.php?site=sports&cnt=36&of=json&callback=jsonp1398130111699&_=1398130992850");
							// 设置header消息
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
							httpget.setHeader("Referer", "http://news.qq.com/");
							httpget.setHeader(
									"User-Agent",
									"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
							try {
								HttpResponse httpResponse = new DefaultHttpClient()
										.execute(httpget);
								if (httpResponse.getStatusLine()
										.getStatusCode() == 200) {
									String result = EntityUtils
											.toString(httpResponse.getEntity());
									String result_1 = result.substring(73);
									System.out.println("science "
											+ result_1.subSequence(0, 1));
									if (result_1.subSequence(0, 1).equals(":")) {
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
									System.out.println("sports " + json_result);
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
								t.setToNow(); // 取得系统时间。
								int year = t.year;
								int month = t.month + 1;
								int monthday = t.monthDay;
								int hour = t.hour;
								int minute = t.minute;
								final String title;
								String desc;
								final String image;
								String link, date, sort, refresh, store;
								title = ms.getLongtitle() + " (体育)";
								image = ms.getImg();
								link = ms.getUrl();
								date = year + "-" + month + "-" + monthday;
								refresh = year + "-" + month + "-" + monthday
										+ " " + hour + ":" + minute;
								desc = ms.getDesc();
								sort = "体育";
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
								mList.add(ns);
							}
							DB db_insert = new DB(Sports.this);
							try {
								db_insert.delete("体育");
								db_insert.insert(mList);
							} catch (Exception e) {
								// TODO: handle exception
								System.out.println("异常:" + e.getMessage());
							}

							mList.clear();
							DB news_db = new DB(Sports.this);
							mList = news_db.check("体育");
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// 将网络图片存放在本地
								if (!tool.isExist("news_image/sports/" + title
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
										// TODO: handle
										// exception
										System.out.println("异常"
												+ e.getMessage());
									}
								}
							}
							Message msg = new Message();
							msg.obj = mList;
							mHandler_refresh.sendMessage(msg);

						}
					}).start();
					break;

				case 1:
					Message msg = new Message();
					msg.obj = mList;
					mHandler_loader.sendMessage(msg);
				default:
					break;

				}

			}
		}.start();
	}

}

