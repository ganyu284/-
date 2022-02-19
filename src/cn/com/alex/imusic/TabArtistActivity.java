package cn.com.alex.imusic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import cn.com.alex.imusic.bean.Artist;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.getdata.GetAllArtists;
import cn.com.alex.imusic.getdata.GetTracksByArtist;
import cn.com.alex.imusic.util.AppExit;
import cn.com.alex.imusic.util.PlayerTimer;
import cn.com.alex.imusic.util.PlayingHelper;

public class TabArtistActivity extends Activity implements OnItemClickListener {
	ArrayList<Artist> artists;
	ArrayList<Music> artistDetail;
	ArrayList<Integer> music_ids;
	ListView lv, dlv;

	View artistView;
	View artistDetailView;
	ViewSwitcher switcher;
	ArtistMusicAdapter adadapter;

	boolean viewFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.artist_list_main);
		switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		setupArtistList();
	}

	private void setupArtistList() {
		
		artists = GetAllArtists.getAllArtists(this);
		ArtistAdapter adapter = new ArtistAdapter(this, 0, artists);
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	private void setupArtistDetailList(int art_id) {
		dlv = (ListView) findViewById(R.id.listView21);
		artistDetail = GetTracksByArtist.getTracksByArtist(this, art_id);
		adadapter = new ArtistMusicAdapter(this, 0, artistDetail);
		dlv.setAdapter(adadapter);
		dlv.setOnItemClickListener(this);
	}

	class ArtistAdapter extends ArrayAdapter<Artist> {

		public ArtistAdapter(Context context, int textViewResourceId,
				List<Artist> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return artists.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) TabArtistActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.artist_list_item, null);
			}

			TextView artist = (TextView) view.findViewById(R.id.textView1);
			artist.setText(artists.get(position).getArtist());
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return artists.get(position).get_id();
		}
	}

	class ArtistMusicAdapter extends ArrayAdapter<Music> {

		public ArtistMusicAdapter(Context context, int textViewResourceId,
				List<Music> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return artistDetail.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			ViewHolder vh;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) TabArtistActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.artist_detail_list_item, null);
				vh = new ViewHolder();

				vh.order = (TextView) view.findViewById(R.id.textView21);
				vh.track = (TextView) view.findViewById(R.id.textView22);
				vh.duration = (TextView) view.findViewById(R.id.textView23);
				view.setTag(vh);
			}else{
				vh = (ViewHolder)view.getTag();
			}
			vh.order.setText(position + 1 + ".");
			vh.track.setText(artistDetail.get(position).getTitle());
			vh.duration.setText(PlayerTimer.format(artistDetail.get(position)
					.getDuration()));
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return artistDetail.get(position).getMusic_id();
		}
		
		class ViewHolder{
			TextView order,track,duration;
		}
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
			this.setupArtistDetailList(artists.get(position).get_id());
			switcher.showNext();
			viewFlag = true;
		} else if (parent == dlv) {
			// 播放音乐
			// PlayingHelper.playMusic(this, artistDetail,position);
			Log.e("KKKK", position + " pos");
			//PlayingHelper.playList = artistDetail;
			PlayingHelper.playMusic(this, position,artistDetail);
		}
	}

	// 头部返回歌手列表按钮处理
	public void back_to_artist(View view) {
		switcher.showPrevious();
		viewFlag = false;
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
}
