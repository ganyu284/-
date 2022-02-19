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
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.getdata.GetAllMusic;
import cn.com.alex.imusic.util.PlayingHelper;

public class TabAllMusicActivity extends Activity {
	private ArrayList<Music> list;

	private int[] imgs = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("TabAllMusicActivity onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.music_list);
		list = GetAllMusic.getFromMediaStore(this);
		MusicAdapter adapter = new MusicAdapter(this, 0, list);
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setItemsCanFocus(true);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 播放音乐
				//PlayingHelper.playMusic(TabAllMusicActivity.this, list,	position);
				//PlayingHelper.playList=list;
				PlayingHelper.playMusic(TabAllMusicActivity.this, position,list);
			}
		});
	}

	class MusicAdapter extends ArrayAdapter<Music> {
		List<Music> aList;

		public MusicAdapter(Context context, int textViewResourceId,
				List<Music> objects) {
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			Music music;
			ViewHolder vh;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.music_list_item, null);
				vh = new ViewHolder();
				vh.title = (TextView) view.findViewById(R.id.ContentTitle);
				vh.album_artist = (TextView) view
						.findViewById(R.id.ContentArtist);
				vh.art = (ImageView) view.findViewById(R.id.image);
				view.setTag(vh);
			}else{
				vh = (ViewHolder) view.getTag();
			}
			music = aList.get(position);
			vh.title.setText(music.getTitle());
			vh.album_artist.setText(music.getAlbum() + " - " + music.getArtist());

			String bmp = music.getCover();

			if (bmp != null) {
				vh.art.setImageURI(Uri.fromFile(new File(bmp)));
			} else {// 因为涉及到重复使用ListView，所以这里一定要加上下面这条语句，否则结果有错
				vh.art.setImageResource(imgs[(int) (Math.random() * 6)]);
			}

			return view;
		}
		
		class ViewHolder {
			TextView title, album_artist;
			ImageView art;
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	// 正在播放按钮处理方法
	public void now_playing(View view) {
		Intent intent = new Intent(this, PlayingActivity.class);
		PlayingHelper.fromActivity = PlayingActivity.class;
		startActivity(intent);
		// this.setContentView(R.layout.now_playing);
	}
}
