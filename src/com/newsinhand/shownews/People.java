package com.newsinhand.shownews;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import com.newsinhand.definition.News;
import com.newsinhand.fileoperate.FileOper;
import com.newsinhand.fileoperate.SaveImage;
import com.newsinhand.refreshnews.Refresh;
import com.newsinhand.refreshnews.Refresh.OnLoadMoreListener;
import com.newsinhand.refreshnews.Refresh.OnRefreshListener;
import com.example.newsinhand.R;

public class People extends Activity {
	private static final String TAG = "People";
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/news_image/" + "people/";
	Bitmap bitmap;
	FileOper tool = new FileOper();
	public static List<News> mList = new ArrayList<News>();
	static boolean state = true;
	private Refresh mListView;
	private int mCount = 10;
	private ArrayList<HashMap<String, Object>> listItem;
	ImageView imageview;
	int index = 0;
	@SuppressWarnings("unchecked")
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			Toast.makeText(People.this, obj, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	});
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
					People.this, listItem,// 数据源
					R.layout.people_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.people_image, R.id.people_title,
							R.id.people_digest });
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
				System.out.println("人物" + ns.getId());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					People.this, listItem,// 数据源
					R.layout.people_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.people_image, R.id.people_title,
							R.id.people_digest });
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
					People.this, listItem,// 数据源
					R.layout.people_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.people_image, R.id.people_title,
							R.id.people_digest });
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
			mListView.onLoadMoreComplete(index); // 加载更多完成
			index = length;
			System.out.println(index+"---------index----------------");//-----------------------------------------------
		}

	};

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		setContentView(R.layout.people);

		mListView = (Refresh) findViewById(R.id.people_list);
		listItem = new ArrayList<HashMap<String, Object>>();
		List<News> db_data = new ArrayList<News>();
		DB news_db = new DB(People.this);
		db_data = news_db.check("人物");
		
		if (db_data.isEmpty() || db_data.size() < 10) {
			if (Yaowen.First_show) {
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
								title = div_1.get(i).getElementsByTag("h2")
										.text()+ "(人物)";									
								link = "http://www.thepaper.cn/"
										+ div_2.get(i).select(".tiptitleImg").attr("href");																
								image =div_2.get(i).select(".tiptitleImg img").attr("src");									
								desc = div_1.get(i).getElementsByTag("p")
										.text();
								date = year + "-" + month + "-" + monthday;
								refresh = year + "-" + month + "-"
										+ monthday + " " + hour + ":"
										+ minute;
								sort = "人物";
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
								//System.out.println(mList.toString()+"----------------mlist-----------------");
							}
							System.out.println(mList.toString()+"------------------mlist-zong---------");//-----------------------------------------------
							DB db_insert = new DB(People.this);
							db_insert.insert(mList);
							mList.clear();
							DB news_check = new DB(People.this);
							mList = news_check.check("人物");
							System.out.println(mList.toString()+"---------check--------------");//-----------------------------------------------
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
						if (Main.people_state == true) {
							for (int i = 0; i < mList.size(); i++) {
								News ns = new News();
								ns = mList.get(i);
								final String image = ns.getImage();
								final String title = ns.getTitle();
								// 将网络图片存放在本地
								if (!tool.isExist("news_image/people/" + title
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
							Main.people_state = false;
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
				if (com.newsinhand.shownews.Main.isNetworkAvailable == true) {
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
				final int index = arg2-2;
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
							System.out.println(link);
							System.out.println(title);
							System.out.println(abstruction);
							Bundle Data = new Bundle();
							Data.putString("news_link", link);
							Data.putString("news_sort", "人物");
							Intent intent = new Intent(People.this,
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
				HashMap<String, Object> map = listItem.get(position - 1);
				final String title = (String) map.get("ItemTitle");
				String abstruction = (String) map.get("ItemText");
				final String link = (String) map.get("ItemLink");
				System.out.println(map.toString()+"-----------------yuyin--------");//-----------------------------------------------
				AlertDialog.Builder builder = new AlertDialog.Builder(
						People.this);
				builder.setMessage("是否语音播报新闻？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										System.out.println(title);
										Bundle Data = new Bundle();
										Data.putString("news_title", title);
										Data.putString("news_link", link);
										Data.putString("news_sort", "人物");
										Intent intent = new Intent(
												People.this,
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
									title = div_1.get(i).getElementsByTag("h2")
											.text()+ "(人物)";									
									link = "http://www.thepaper.cn/"
											+ div_2.get(i).select(".tiptitleImg").attr("href");																
									image =div_2.get(i).select(".tiptitleImg img").attr("src");									
									desc = div_1.get(i).getElementsByTag("p")
											.text();
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									sort = "人物";
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
								DB db_insert = new DB(People.this);
								try {
							        db_insert.delete("人物");
									db_insert.insert(mList);
								} catch (Exception e) {
									// TODO: handle exception
									System.out.println("异常:" + e.getMessage());
								}

								mList.clear();
								DB news_db = new DB(People.this);
								mList = news_db.check("人物");
								for (int i = 0; i < mList.size(); i++) {
									News ns = new News();
									ns = mList.get(i);
									final String image = ns.getImage();
									final String title = ns.getTitle();
									// 将网络图片存放在本地
									if (!tool.isExist("news_image/people/"
											+ title + ".jpeg")) {
										try {
											URL url = new URL(image);
											InputStream is = url.openStream();
											bitmap = BitmapFactory
													.decodeStream(is);
											SaveImage save = new SaveImage();
											bitmap = save.setImage(bitmap);
											save.saveFile(bitmap, ALBUM_PATH,
													title + ".jpeg");
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
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
