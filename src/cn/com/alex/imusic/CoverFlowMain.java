package cn.com.alex.imusic;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.alex.imusic.bean.Album;
import cn.com.alex.imusic.flip3d.DisplayNextView;
import cn.com.alex.imusic.flip3d.Flip3DAnimation;
import cn.com.alex.imusic.getdata.GetAllAlbum;
import cn.com.alex.imusic.util.AppExit;
import cn.com.alex.imusic.util.ImageUtil;

public class CoverFlowMain extends Activity implements OnItemSelectedListener,
		AdapterView.OnItemClickListener {
	private int screenWidth, screenHeight;
	private TextView title, artist;

	private ArrayList<Album> albums;

	private boolean isFirstImage = true;
	// ר����Ϣ�л���2��View���Լ���Layout
	private TextView flipTv;
	private View flipAlbumInfo;
	// private FrameLayout flipLayout;

	private CoverFlow coverFlow;

	private int selectedPos = 0;
	private Album album;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.coverflow);

		Intent intent = this.getIntent();
		int album_id = intent.getIntExtra("album_id", -1);
		if (album_id != -1) {
			album = new Album(album_id);
		}
		System.out.println("album_id="+ album_id);
		Display display = getWindowManager().getDefaultDisplay();

		albums = GetAllAlbum.getAllAlbums(this);
		// ��ר���б��У�ȡ����Ӧר����������
		selectedPos = albums.indexOf(album);
		System.out.println("selectedPos="+selectedPos);
		if (selectedPos == -1)
			selectedPos = 0;

		screenWidth = display.getHeight();
		screenHeight = display.getHeight();

		coverFlow = (CoverFlow) findViewById(R.id.gallery1);
		coverFlow.setAdapter(new ImageAdapter(this));
		coverFlow.setOnItemSelectedListener(this);
		coverFlow.setOnItemClickListener(this);
		
		title = (TextView) findViewById(R.id.textView1);
		artist = (TextView) findViewById(R.id.textView3);

		// ר����Ϣ������
		// ������ʾר����Ϣ��2����ͼ�����е�һ����ͼΪ͸����ͼ��ֻ��Ϊ��ʵ�ֶ��������ϵģ�û��ʵ������
		flipTv = (TextView) findViewById(R.id.textView2);
		// flipAlbumInfo = (LinearLayout) findViewById(R.id.albuminfo);
		flipAlbumInfo = (LinearLayout) findViewById(R.id.albuminfo);
		flipAlbumInfo.setVisibility(View.GONE);
		flipAlbumInfo.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickToShowAlbumInfo();
			}
		});
		// flipLayout = (FrameLayout) findViewById(R.id.frameLayout1);
		// System.out.println("flipLayot:" + flipLayout);

		coverFlow.setSpacing(-145);
		coverFlow.setSelection(selectedPos, true);
		coverFlow.setAnimationDuration(1000);

		// System.out.println("CoverFlowMain onCreate" + title);
		// setContentView(coverFlow);
		AppExit.allActivity.add(this);

	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public ImageView getReflectionImage(String uri) {

			// ���Ͳ����ʣ����ⷢ��OutOfMemory�Ĵ���
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// System.out.println(computeSampleSize(options, -1, 128*128));
			options.inSampleSize = 2;

			// ���Զ���Ӧ���»ָ���false
			options.inJustDecodeBounds = false;

			// ��resource�н�ͼƬת����Bitmap
			Bitmap originalImage = null;
			if (uri != null) {
				originalImage = BitmapFactory.decodeFile(uri);
			} else {
				originalImage = BitmapFactory.decodeResource(getResources(),
						R.drawable.earphone, options);
			}
			Bitmap convertImage = ImageUtil
					.createReflectionImageWithOrigin(originalImage);

			ImageView imageView = new ImageView(mContext);
			imageView.setImageBitmap(convertImage);
			imageView
					.setLayoutParams(new CoverFlow.LayoutParams(
							(int) (screenWidth * 7 / 10),
							(int) (screenHeight * 4 / 5)));// (int)(screenWidth*10/10)));

			originalImage.recycle();

			return imageView;
		}

		public int getCount() {
			return albums.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = getReflectionImage(albums.get(position)
					.getAlbum_art());
			// ���������
			BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
			drawable.setAntiAlias(true);
			return iv;
		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views depending on the
		 * 'offset' to the center.
		 */
		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
	}

	
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		artist.setText(albums.get(position).getArtist());
		title.setText(albums.get(position).getTitle());
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		clickToShowAlbumInfo();
	}

	private void clickToShowAlbumInfo() {
		if (isFirstImage) {
			applyRotation(0, 90, flipTv, flipAlbumInfo);
			isFirstImage = !isFirstImage;

		} else {
			applyRotation(0, -90, flipTv, flipAlbumInfo);
			isFirstImage = !isFirstImage;
		}
	}

	// ��ʾר����Ϣ
	private void applyRotation(float start, float end, View view1, View view2) {
		// Find the center of View
		final float centerX = view2.getWidth() / 2.0f;
		final float centerY = view2.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Flip3DAnimation rotation = new Flip3DAnimation(start, end,
				centerX, centerY);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(isFirstImage, view1,
				view2));

		if (isFirstImage) {
			view1.startAnimation(rotation);
			// flipView1.startAnimation(rotation);
		} else {
			view2.startAnimation(rotation);
			// flipView2.startAnimation(rotation);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		return;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		this.finish();
		/*
		 * if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
		 * Intent intent; if (fromWhere == PlayListHelper.FROM_MAIN) { intent =
		 * new Intent(this, MainActivity.class); } else { intent = new
		 * Intent(this, PlayingActivity.class); } startActivity(intent); }
		 */
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("CoverFlowMain onSaveInstanceState");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("CoverFlowMain onResume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("CoverFlowMain onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("CoverFlowMain onStop");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("CoverFlowMain onStart");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("CoverFlowMain onRestart");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("CoverFlowMain onNewIntent");
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		System.out.println("CoverFlowMain onRetainNonConfigurationInstance");
		return super.onRetainNonConfigurationInstance();

	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		System.out.println("CoverFlowMain onRestoreInstanceState");
		super.onRestoreInstanceState(state);
	}

}