package com.drocode.swithcer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class ImageActivity extends Activity {
	// public HashMap<String,Bitmap> imagesCache=new HashMap<String,
	// Bitmap>();//ͼƬ����
	public List<String> urls;
	public GuideGallery images_ga;
	private int positon = 0;
	private Thread timeThread = null;
	public boolean timeFlag = true;
	private boolean isExit = false;
	public ImageTimerTask timeTaks = null;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.url_connection_image);
		timeTaks = new ImageTimerTask();
		autoGallery.scheduleAtFixedRate(timeTaks, 5000, 5000);
		timeThread = new Thread() {
			@SuppressLint("NewApi")
			public void run() {
				while (!isExit) {
					try {
						Thread.sleep(1500);
						overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (timeTaks) {
						if (!timeFlag) {
							timeTaks.timeCondition = true;
							timeTaks.notifyAll();
						}
					}
					timeFlag = true;
				}
			};
		};
		timeThread.start();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		init();
	}

	private void init() {
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		// imagesCache.put("background_non_load",image); //���û�����Ĭ�ϵ�ͼƬ

		images_ga = (GuideGallery) findViewById(R.id.image_wall_gallery);
		images_ga.setImageActivity(this);

		ImageAdapter imageAdapter = new ImageAdapter(this);
		images_ga.setAdapter(imageAdapter);
		LinearLayout pointLinear = (LinearLayout) findViewById(R.id.gallery_point_linear);
		pointLinear.setBackgroundColor(Color.argb(200, 135, 135, 152));
		for (int i = 0; i < 4; i++) {
			ImageView pointView = new ImageView(this);
			if (i == 0) {
				pointView.setBackgroundResource(R.drawable.feature_point_cur);
			} else {
				pointView.setBackgroundResource(R.drawable.feature_point);
			}
			pointLinear.addView(pointView);
		}
		images_ga.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				System.out.println(arg2 + "arg2");
				/*switch (arg2) {
				case 0:
					uri = Uri.parse("http://www.36939.net/");
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);

					break;
				case 1:
					uri = Uri.parse("http://www.jiqunejia.com/default.aspx");
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);

					break;
				case 2:
					uri = Uri.parse("http://www.jiqunejia.tv/");
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);

					break;
				case 3:
					uri = Uri.parse("http://city.4000100006.com/");
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);

					break;

				default:
					break;
				}*/

			}
		});

	}

	public void changePointView(int cur) {
		LinearLayout pointLinear = (LinearLayout) findViewById(R.id.gallery_point_linear);
		View view = pointLinear.getChildAt(positon);
		View curView = pointLinear.getChildAt(cur);
		if (view != null && curView != null) {
			ImageView pointView = (ImageView) view;
			ImageView curPointView = (ImageView) curView;
			pointView.setBackgroundResource(R.drawable.feature_point);
			curPointView.setBackgroundResource(R.drawable.feature_point_cur);
			positon = cur;
		}
	}

	final Handler autoGalleryHandler = new Handler() {
		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case 1:
				images_ga.setSelection(message.getData().getInt("pos"));
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		timeFlag = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timeTaks.timeCondition = false;
	}

	class ImageTimerTask extends TimerTask {
		public volatile boolean timeCondition = true;

		// int gallerypisition = 0;
		@SuppressLint("NewApi")
		public void run() {
			synchronized (this) {
				while (!timeCondition) {
					try {
						Thread.sleep(100);
						wait();
						overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
			try {
				gallerypisition = images_ga.getSelectedItemPosition() + 1;
				System.out.println(gallerypisition + "");
				Message msg = new Message();
				Bundle date = new Bundle();// ������
				date.putInt("pos", gallerypisition);
				msg.setData(date);
				msg.what = 1;// ��Ϣ��ʶ
				autoGalleryHandler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Timer autoGallery = new Timer();

}