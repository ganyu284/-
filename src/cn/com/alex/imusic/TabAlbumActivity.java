package cn.com.alex.imusic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import cn.com.alex.imusic.bean.Album;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.getdata.GetAllAlbum;
import cn.com.alex.imusic.getdata.GetTracksByAlbum;
import cn.com.alex.imusic.util.AppExit;
import cn.com.alex.imusic.util.PlayerTimer;
import cn.com.alex.imusic.util.PlayingHelper;

public class TabAlbumActivity extends Activity implements
		AdapterView.OnItemClickListener {
	private ArrayList<Album> list;
	private ArrayList<Music> albumDetail;
	ListView lv;
	ListView dlv;
	AlbumMusicAdapter mdadapter;
	ImageView art;
	TextView artist, title, duration;

	private int[] imgs = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g };
	ViewSwitcher switcher;
	View albumView, albumDetailView;
	boolean viewFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.album_list_main);
		switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		setupAlbumView();
	}

	@Override
	public void onBackPressed() {
		if (viewFlag) {
			viewFlag = false;
			switcher.showPrevious();
		} else {
			AppExit.exitApp(this);
		}
	}

	private void setupAlbumView() {
		list = GetAllAlbum.getAllAlbums(this);
		AlbumAdapter adapter = new AlbumAdapter(this, 0, list);
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	private void setupAlbumDetailList(Album album) {
		albumDetail = GetTracksByAlbum.getTracksByAlbum(this,
				album.getAlbum_id());
		AlbumMusicAdapter mdadapter = new AlbumMusicAdapter(this, 0,
				albumDetail);

		dlv = (ListView) findViewById(R.id.listView21);

		art = (ImageView) findViewById(R.id.imageView1);
		artist = (TextView) findViewById(R.id.textView1);
		title = (TextView) findViewById(R.id.textView2);
		duration = (TextView) findViewById(R.id.textView3);

		dlv.setAdapter(mdadapter);
		dlv.setOnItemClickListener(this);

		artist.setText(album.getArtist());
		title.setText(album.getTitle());

		String bmp = album.getAlbum_art();
		if (bmp != null) {
			art.setImageURI(Uri.fromFile(new File(bmp)));
		} else {
			art.setImageResource(imgs[(int) (Math.random() * 6)]);
		}
		duration.setText("共" + album.getNum_of_songs() + "首歌");
	}

	class AlbumAdapter extends ArrayAdapter<Album> {
		List<Album> aList;

		public AlbumAdapter(Context context, int textViewResourceId,
				List<Album> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			aList = objects;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return aList.size();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return aList.get(position).getAlbum_id();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			Album album;
			ViewHolder vh;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.album_list_item, null);
				vh = new ViewHolder();
				vh.title = (TextView) view.findViewById(R.id.ContentTitle);
				vh.album_artist = (TextView) view
						.findViewById(R.id.ContentArtist);
				vh.art = (ImageView) view.findViewById(R.id.image);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}
			album = aList.get(position);
			vh.title.setText(album.getTitle());
			vh.album_artist.setText(album.getArtist());

			String bmp = album.getAlbum_art();
			// GetAlbumArt.getAlbumArt(TabAllAlbumActivity.this, "" +
			// music.getAlbum_id());
			if (bmp != null) {
				vh.art.setImageURI(Uri.fromFile(new File(bmp)));
			} else {// 因为涉及到重复使用ListView，所以这里一定要加上下面这条语句，否则结果有错
				vh.art.setImageResource(imgs[(int) (Math.random() * 6)]);
			}
			bmp = null;
			return view;
		}

		class ViewHolder {
			TextView title, album_artist;
			ImageView art;
		}
	}

	class AlbumMusicAdapter extends ArrayAdapter<Music> {

		public AlbumMusicAdapter(Context context, int textViewResourceId,
				List<Music> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return albumDetail.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			ViewHolder vh;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) TabAlbumActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.album_detail_list_item, null);
				vh = new ViewHolder();
				vh.order = (TextView) view.findViewById(R.id.textView21);
				vh.track = (TextView) view.findViewById(R.id.textView22);
				vh.duration = (TextView) view.findViewById(R.id.textView23);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}
			vh.order.setText(position + 1 + ".");
			vh.track.setText(albumDetail.get(position).getTitle());
			vh.duration.setText(PlayerTimer.format(albumDetail.get(position)
					.getDuration()));
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return albumDetail.get(position).getMusic_id();
		}

		class ViewHolder {
			TextView order, track, duration;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// 正在播放按钮处理方法
	public void now_playing(View view) {
		Intent intent = new Intent(this, PlayingActivity.class);
		PlayingHelper.fromActivity = PlayingActivity.class;
		startActivity(intent);
		// this.setContentView(R.layout.now_playing);
	}


	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent == lv) {
			this.setupAlbumDetailList(list.get(position));
			viewFlag = true;
			switcher.showNext();
		} else if (parent == dlv) {
			// 播放音乐
			// PlayingHelper.playMusic(this, albumDetail,position);
			// PlayingHelper.playList = albumDetail;
			PlayingHelper.playMusic(this, position, albumDetail);
		}
	}

	// 头部返回专辑列表按钮处理
	public void back_to_album(View view) {
		switcher.showPrevious();
		viewFlag = false;
	}
}
