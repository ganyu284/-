package cn.com.alex.imusic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.getdata.GetAllMusic;
import cn.com.alex.imusic.util.PlayListHelper;

public class AddMusic2PlaylistActivity extends Activity {
	private ArrayList<Music> list = new ArrayList<Music>();
	private MusicAdapter adapter;
	//private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.music_list_edit);
		adapter = new MusicAdapter(this, 0, list);
		//pb = (ProgressBar) findViewById(R.id.progressBar1);
		ListView lv = (ListView) findViewById(R.id.listView1);

		lv.setEmptyView(findViewById(android.R.id.empty));
		lv.setAdapter(adapter);
		lv.setItemsCanFocus(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.toggle(position);
			}
		});
		CheckBox checkAll = (CheckBox) findViewById(R.id.checkAllBox1);
		checkAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				adapter.checkUnckeckAll(isChecked);
			}
		});
		new ReadMusicTask().execute();
	}

	class MusicAdapter extends ArrayAdapter<Music> {
		List<Music> aList;
		boolean[] itemStatus;

		public MusicAdapter(Context context, int textViewResourceId,
				List<Music> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			aList = objects;

		}

		public void initItemStatus() {
			itemStatus = new boolean[aList.size()];
		}

		// 全选、全不选
		public void checkUnckeckAll(boolean isChecked) {

			for (int i = 0; i < itemStatus.length; i++) {
				itemStatus[i] = isChecked;
			}
			this.notifyDataSetChanged();
		}

		public void toggle(int position) {
			if (itemStatus[position] == true) {
				itemStatus[position] = false;
			} else {
				itemStatus[position] = true;
			}
			this.notifyDataSetChanged();// date changed and we should refresh
										// the view
		}

		public int[] getSelectedItemIndexes() {

			if (itemStatus == null || itemStatus.length == 0) {
				return new int[0];
			} else {
				int size = itemStatus.length;
				int counter = 0;
				// TODO how can we skip this iteration?
				for (int i = 0; i < size; i++) {
					if (itemStatus[i] == true)
						++counter;
				}
				int[] selectedIndexes = new int[counter];
				int index = 0;
				for (int i = 0; i < size; i++) {
					if (itemStatus[i] == true)
						selectedIndexes[index++] = i;
				}
				return selectedIndexes;
			}
		};

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
				view = inflater.inflate(R.layout.music_list_item_edit, null);
				vh = new ViewHolder();
				vh.title = (TextView) view.findViewById(R.id.ContentTitle);
				vh.album_artist = (TextView) view
						.findViewById(R.id.ContentArtist);
				vh.art = (ImageView) view.findViewById(R.id.image);
				vh.cb = (CheckBox) view.findViewById(R.id.checkBox1);
				// vh.cb.setChecked(checked);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}
			music = aList.get(position);
			vh.title.setText(music.getTitle());
			vh.album_artist.setText(music.getAlbum() + " - "
					+ music.getArtist());
			// 加上监听器
			vh.cb.setOnCheckedChangeListener(new MyCheckBoxChangedListener(
					position));
			// 根据数组中的状态来更新checkbox选中状态
			vh.cb.setChecked(itemStatus[position]);

			String bmp = music.getCover();

			if (bmp != null) {
				vh.art.setImageURI(Uri.fromFile(new File(bmp)));
			} else {// 因为涉及到重复使用ListView，所以这里一定要加上下面这条语句，否则结果有错
				vh.art.setImageResource(R.drawable.c);
			}

			return view;
		}

		class ViewHolder {
			TextView title, album_artist;
			ImageView art;
			CheckBox cb;
		}

		class MyCheckBoxChangedListener implements OnCheckedChangeListener {
			int position;

			MyCheckBoxChangedListener(int position) {
				this.position = position;
			}

			
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// System.out.println("" + position + "Checked?:" + isChecked);
				if (isChecked)
					itemStatus[position] = true;
				else
					itemStatus[position] = false;
			}
		}
	}



	// OK
	public void savePlayList(View view) {
		// 发送一个广播
		// ArrayList<Music> tmpAry = new ArrayList<Music>();
		int[] checkedIdx = adapter.getSelectedItemIndexes();
		int[] musics = new int[checkedIdx.length];
		for (int i = 0; i < checkedIdx.length; i++) {
			// tmpAry.add(list.get(checkedIdx[i]));
			musics[i] = list.get(checkedIdx[i]).getMusic_id();
		}

		Intent saveIntent = new Intent(
				PlayListHelper.INTENT_ACTION_ADD_MUSIC_PLAYLIST);
		// saveIntent.putParcelableArrayListExtra("musics", tmpAry);
		saveIntent.putExtra("musics", musics);
		sendBroadcast(saveIntent);
		this.finish();
	}

	public void cancel(View view) {
		this.finish();
	}

	// 读取所有音乐
	class ReadMusicTask extends AsyncTask<Void, String, ArrayList<Music>> {

		@Override
		protected ArrayList<Music> doInBackground(Void... voids) {
			// TODO Auto-generated method stub

			return GetAllMusic
					.getFromMediaStore(AddMusic2PlaylistActivity.this);
			// return list;
		}

		@Override
		protected void onPostExecute(ArrayList<Music> result) {
			list.addAll(result);
			adapter.notifyDataSetChanged();
			//System.out.println(adapter.isEmpty()+">><<");
			adapter.initItemStatus();
			//pb.setVisibility(View.INVISIBLE);
		}
	}
}
