package com.newsinhand.shownews;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newsinhand.R;
import com.newsinhand.database.DB;
import com.newsinhand.definition.News;

public class TTS extends Activity {
	private TextToSpeech mSpeech;
	private TextView mTitle;
	private TextView mContent;
	private EditText mEditText;
	private ImageButton backBtn;
	private ImageButton setBtn;
	PopupWindow popupWindow;
	private WindowManager mWindowManager;
	private View myView;
	private CheckBox stop_btn;
	private SeekBar light_value, voice_speed;
	private Spinner text_size, text_color;
	String content="";
	View parent;
	private Context context;
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			String obj = (String) msg.obj;
			mContent.setText(obj);
			mSpeech.speak(obj, TextToSpeech.QUEUE_FLUSH, null);
			return false;
		}
	});

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		mWindowManager = (WindowManager) getSystemService(TTS.WINDOW_SERVICE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tts);
		Intent intent = getIntent();
		final String title = (String) intent.getSerializableExtra("news_title");
		final String link = (String) intent.getSerializableExtra("news_link");
		final String sort = (String) intent.getSerializableExtra("news_sort");
		System.out.println(title);
		System.out.println(link);
		System.out.println(sort);
		
		TextView title_text=(TextView)findViewById(R.id.title_text);
		title_text.setText(sort);
		mTitle = (TextView) findViewById(R.id.voice_title);
		mContent = (TextView) findViewById(R.id.voice_content);
		mContent.setMovementMethod(new ScrollingMovementMethod());
		mTitle.setText(title);
		mSpeech = new TextToSpeech(this, new OnInitListener() {
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					int result = mSpeech.setLanguage(Locale.CHINA);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						System.out.println("not use");
					} else {
						mSpeech.stop();
					}
				}
			}

		});
		DB news_check = new DB(TTS.this);
		News ns=news_check.checkNews(title,sort);
		System.out.println(ns.getId());
		if(ns.get_content().isEmpty()){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Document doc = Jsoup.connect(link).get();
					Elements p = doc
							.select("div#Cnt-Main-Article-QQ[bosszone] p[style=TEXT-INDENT: 2em]");
					if (p.size() != 0) {
						content = "       ";
						for (org.jsoup.nodes.Element element : p) {
							content += element.text() + "\n\r" + "       ";
						}
					} else {
						content ="";
						Elements div = doc
								.select("div#Cnt-Main-Article-QQ[bosszone] p");
						for (org.jsoup.nodes.Element element : div) {
							content += element.text() + "\n\r";
						}
					}
					DB news_db = new DB(TTS.this);
					news_db.update(content, title);
					Message msg = new Message();
					msg.obj = content;
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}).start();
		}
		else{
			Message msg = new Message();
			msg.obj = ns.get_content();
			handler.sendMessage(msg);
		}
		backBtn = (ImageButton) this.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		setBtn = (ImageButton) this.findViewById(R.id.set_btn);
		setBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				View contentView = getLayoutInflater().inflate(
						R.layout.popup, null);
				// 初始化控件

				voice_speed = (SeekBar) contentView
						.findViewById(R.id.tts_voice_speed);
				voice_speed.setProgress(30);
				voice_speed
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
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											String content = "       ";
											Document doc = Jsoup.connect(link)
													.get();
											Elements p = doc
													.select("div#Cnt-Main-Article-QQ[bosszone] p[style=TEXT-INDENT: 2em]");
											if (p.size() != 0) {
												for (org.jsoup.nodes.Element element : p) {
													content += element.text()
															+ "\n\r"
															+ "       ";
												}
												Message msg = new Message();
												msg.obj = content;
												handler.sendMessage(msg);
											} else {
												content = "";
												Elements div = doc
														.select("div#Cnt-Main-Article-QQ[bosszone] p");
												for (org.jsoup.nodes.Element element : div) {
													content += element.text()
															+ "\n\r";
												}
												Message msg = new Message();
												msg.obj = content;
												handler.sendMessage(msg);
											}
										} catch (Exception e) {
										}
									}
								}).start();
							}

						});
				light_value = (SeekBar) contentView
						.findViewById(R.id.tts_luminance_value);
				light_value.setProgress(100);
				light_value
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// TODO Auto-generated method stub
								WindowManager.LayoutParams params = TTS.this
										.getWindow().getAttributes();
								params.screenBrightness = progress / 255f;
								TTS.this.getWindow().setAttributes(
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

				stop_btn = (CheckBox) contentView
						.findViewById(R.id.tts_voice_stop);
				stop_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							mSpeech.stop();
						} else {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										String content = "       ";
										Document doc = Jsoup.connect(link)
												.get();
										Elements p = doc
												.select("div#Cnt-Main-Article-QQ[bosszone] p[style=TEXT-INDENT: 2em]");
										if (p.size() != 0) {
											for (org.jsoup.nodes.Element element : p) {
												content += element.text()
														+ "\n\r" + "       ";
											}
											Message msg = new Message();
											msg.obj = content;
											handler.sendMessage(msg);
										} else {
											content = "";
											Elements div = doc
													.select("div#Cnt-Main-Article-QQ[bosszone] p");
											for (org.jsoup.nodes.Element element : div) {
												content += element.text()
														+ "\n\r";
											}
											Message msg = new Message();
											msg.obj = content;
											handler.sendMessage(msg);
										}
									} catch (Exception e) {
									}
								}
							}).start();
						}
					}

				});
				List<String> list = new ArrayList<String>();
				list.add("12sp");
				list.add("14sp");
				list.add("16sp");
				list.add("18sp");
				list.add("20sp");
				list.add("22sp");
				list.add("24sp");
				list.add("26sp");
				list.add("28sp");
				list.add("30sp");
				text_size = (Spinner) contentView
						.findViewById(R.id.tts_text_size);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item, list);
				adapter.setDropDownViewResource(R.layout.spinner_item);
				text_size.setAdapter(adapter);
				text_size
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								TextView tv = (TextView) arg1;
								tv.setGravity(Gravity.CENTER);
								tv.setTextColor(getResources().getColor(
										R.color.skyblue));
								mContent.setTextSize(arg2 * 2 + 12);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								mContent.setTextSize(16);
							}

						});
				List<String> list_color = new ArrayList<String>();
				list_color.add("黑色");
				list_color.add("粉红色");
				list_color.add("金色");
				list_color.add("紫红色");
				list_color.add("红色");
				list_color.add("巧可力色");
				list_color.add("天蓝色");
				list_color.add("紫色");
				list_color.add("绿色");
				text_color= (Spinner) contentView
						.findViewById(R.id.tts_text_color);
				ArrayAdapter<String> adapter_color = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item,
						list_color);
				adapter_color.setDropDownViewResource(R.layout.spinner_item);
				text_color.setAdapter(adapter_color);
				text_color
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
									mContent.setTextColor(getResources().getColor(
											R.color.black));
									break;
								case 1:
									mContent.setTextColor(getResources().getColor(
											R.color.lightpink));
									break;
								case 2:
									mContent.setTextColor(getResources().getColor(
											R.color.pink));
									break;
								case 3:
									mContent.setTextColor(getResources().getColor(
											R.color.magenta));
									break;
								case 4:
									mContent.setTextColor(getResources().getColor(
											R.color.oldlace));
									break;
								case 5:
									mContent.setTextColor(getResources().getColor(
											R.color.peru));
									break;
								case 6:
									mContent.setTextColor(getResources().getColor(
											R.color.skyblue));
									break;
								case 7:
									mContent.setTextColor(getResources().getColor(
											R.color.maroon));
									break;
								case 8:
									mContent.setTextColor(getResources().getColor(
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

						});  
				popupWindow = new PopupWindow(contentView,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						true);
				parent = getLayoutInflater().inflate(R.layout.tts,
						null);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.setAnimationStyle(R.style.PopupAnimation);
				popupWindow.showAtLocation(parent, Gravity.CENTER
						| Gravity.CENTER, 0, 0);

				popupWindow.update();
			}
		}); 
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mSpeech != null) {
			mSpeech.stop();
			mSpeech.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tts, menu);
		return true;
	}

}
