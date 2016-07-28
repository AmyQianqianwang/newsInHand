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
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.example.newsinhand.R;
import com.newsinhand.database.DB;
import com.newsinhand.definition.News;
import com.newsinhand.fileoperate.FileOper;
import com.newsinhand.fileoperate.SaveImage;
import com.newsinhand.refreshnews.Refresh;
import com.newsinhand.refreshnews.Refresh.OnLoadMoreListener;
import com.newsinhand.refreshnews.Refresh.OnRefreshListener;


public class Yaowen extends Activity {
	private static final String TAG = "Yaowen";
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/news_image/" + "yaowen/";
	Bitmap bitmap;
	private TextToSpeech mSpeech;
	FileOper tool = new FileOper();
	public static boolean First_show = false;
	public static List<News> mList = new ArrayList<News>();
	// private CustomListAdapter mAdapter;
	private Refresh mListView;
	private int mCount = 10;
	private ArrayList<HashMap<String, Object>> listItem;
	ImageView imageview;
	int index = 0;
	int list_index = 0;
	private ImageButton voiceBtn;
	@SuppressWarnings("unchecked")
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			Toast.makeText(Yaowen.this, obj, Toast.LENGTH_SHORT)
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
				System.out.println("Ҫ��" + ns.getId());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				map.put("ItemVoice", R.drawable.voip_speaker_off);
				listItem.add(map);

			}
			// Toast.makeText(XinwenActivity.this,String.valueOf(jsonArray.length()),
			// Toast.LENGTH_LONG).show();
			// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Yaowen.this, listItem,// ����Դ
					R.layout.yaowen_item,// ListItem��XMLʵ��
					// ��̬������ImageItem��Ӧ������
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem��XML�ļ������һ��ImageView,����TextView ID
					new int[] { R.id.yaowen_image, R.id.yaowen_title,
							R.id.yaowen_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// �ж��Ƿ�Ϊ����Ҫ����Ķ���
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// ��Ӳ�����ʾ
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
				System.out.println("Ҫ��" + ns.getId());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Yaowen.this, listItem,// ����Դ
					R.layout.yaowen_item,// ListItem��XMLʵ��
					// ��̬������ImageItem��Ӧ������
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem��XML�ļ������һ��ImageView,����TextView ID
					new int[] { R.id.yaowen_image, R.id.yaowen_title,
							R.id.yaowen_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// �ж��Ƿ�Ϊ����Ҫ����Ķ���
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// ��Ӳ�����ʾ
			mListView.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
			index = 10;
			System.out.println("����ˢ��");
			mListView.onRefreshComplete(); // ����ˢ�����
			System.out.println("����ˢ�����");
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
				System.out.println(ns.getTitle());
				System.out.println(ns.getId());
				map.put("ItemText", ns.getDesc());
				map.put("ItemLink", ns.getLink());
				listItem.add(map);
			}
			// Toast.makeText(XinwenActivity.this,String.valueOf(jsonArray.length()),
			// Toast.LENGTH_LONG).show();
			// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					Yaowen.this, listItem,// ����Դ
					R.layout.yaowen_item,// ListItem��XMLʵ��
					// ��̬������ImageItem��Ӧ������
					new String[] { "ItemImage", "ItemTitle", "ItemText" },
					// ImageItem��XML�ļ������һ��ImageView,����TextView ID
					new int[] { R.id.yaowen_image, R.id.yaowen_title,
							R.id.yaowen_digest });
			listItemAdapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// �ж��Ƿ�Ϊ����Ҫ����Ķ���
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						return true;
					} else
						return false;
				}
			});
			// ��Ӳ�����ʾ
			mListView.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
			mListView.onLoadMoreComplete(index); // ���ظ������
			index = length;
		}

	};

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		setContentView(R.layout.yaowen);
		mListView = (Refresh) findViewById(R.id.Yaowen_list);
		listItem = new ArrayList<HashMap<String, Object>>();
		List<News> db_data = new ArrayList<News>();
		DB news_db = new DB(Yaowen.this);
		db_data = news_db.check("Ҫ��");
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
							title = div_1.get(i).select(".linkto").text()
									+ " (Ҫ��)";
							
							image = div_1.get(i).select(".picto").attr("src");
							link = div_1.get(i).select(".linkto").attr("href");
							date = year + "-" + month + "-" + monthday;
							refresh = year + "-" + month + "-" + monthday + " "
									+ hour + ":" + minute;
							desc = div_1.get(i).getElementsByTag("p").text();
							sort = "Ҫ��";
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
						DB db_insert = new DB(Yaowen.this);
						db_insert.insert(mList);
						mList.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DB news_check = new DB(Yaowen.this);
					mList = news_check.check("Ҫ��");
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
								save.saveFile(bitmap, ALBUM_PATH, title
										+ ".jpeg");
								is.close();
							} catch (Exception e) {
								// TODO: handle exception
								System.out.println("�쳣" + e.getMessage());
							}
						}
					}
					Message msg = new Message();
					msg.obj = mList;
					mHandler_initiate.sendMessage(msg);
					First_show = true;
				}
			}).start();
		} else {
			Message msg = new Message();
			msg.obj = db_data;
			mHandler_initiate.sendMessage(msg);
			mList = db_data;
			First_show = true;
		}
		initView();
	}

	private void initView() {
		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				// TODO ����ˢ��
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
				// TODO ���ظ���
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				list_index = arg2 - 1;
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (Main.isNetworkAvailable) {
							HashMap<String, Object> map = listItem
									.get(list_index);
							String title = (String) map.get("ItemTitle");
							String abstruction = (String) map.get("ItemText");
							String link = (String) map.get("ItemLink");
							Bundle Data = new Bundle();
							Data.putString("news_link", link);
							Data.putString("news_sort", "Ҫ��");
							Intent intent = new Intent(Yaowen.this,
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
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent,
					final View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map = listItem.get(position - 1);
				final String title = (String) map.get("ItemTitle");
				String abstruction = (String) map.get("ItemText");
				final String link = (String) map.get("ItemLink");
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Yaowen.this);
				builder.setMessage("�Ƿ������������ţ�")
						.setPositiveButton("��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										System.out.println(title);
										Bundle Data = new Bundle();
										Data.putString("news_title", title);
										Data.putString("news_link", link);
										Data.putString("news_sort", "Ҫ��");
										Intent intent = new Intent(
												Yaowen.this,
												TTS.class);
										intent.putExtras(Data);
										startActivity(intent);

									}
								}).setNegativeButton("��", null);
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
				msg_show.obj = "���������ӣ�ˢ��ʧ��!";
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
									final int id = i;
									String desc;
									final String image;
									String link, date, sort, refresh, store;
									title = div_1.get(i).select(".linkto")
											.text()
											+ " (Ҫ��)";
									//System.out.println(title+"1111111111111111***********************************************************");
									image = div_1.get(i).select(".picto")
											.attr("src");
									link = div_1.get(i).select(".linkto")
											.attr("href");
									date = year + "-" + month + "-" + monthday;
									refresh = year + "-" + month + "-"
											+ monthday + " " + hour + ":"
											+ minute;
									desc = div_1.get(i).getElementsByTag("p")
											.text();
									sort = "Ҫ��";
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
								DB db_insert = new DB(Yaowen.this);
								try {
									db_insert.delete("Ҫ��");
									db_insert.insert(mList);
								} catch (Exception e) {
									System.out.println("�쳣:" + e.getMessage());
								}
								mList.clear();

								DB news_db = new DB(Yaowen.this);
								mList = news_db.check("Ҫ��");

								for (int i = 0; i < mList.size(); i++) {
									News ns = new News();
									ns = mList.get(i);
									final String image = ns.getImage();
									final String title = ns.getTitle();
									// ������ͼƬ����ڱ���
									if (!tool.isExist("news_image/yaowen/"
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
											System.out.println("�쳣"
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

	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mSpeech != null) {
			mSpeech.stop();
			mSpeech.shutdown();
		}
		super.onDestroy();
	}
}

