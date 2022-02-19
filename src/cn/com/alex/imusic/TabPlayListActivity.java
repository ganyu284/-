package cn.com.alex.imusic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.bean.Playlist;
import cn.com.alex.imusic.getdata.AddMusicToPlaylist;
import cn.com.alex.imusic.getdata.DeleteMusicFromPlaylist;
import cn.com.alex.imusic.getdata.GetPlaylist;
import cn.com.alex.imusic.getdata.GetTracksByPlaylist;
import cn.com.alex.imusic.getdata.PlaylistManager;
import cn.com.alex.imusic.util.AppExit;
import cn.com.alex.imusic.util.PlayListHelper;
import cn.com.alex.imusic.util.PlayingHelper;

public class TabPlayListActivity extends Activity implements
		AdapterView.OnItemClickListener {
	private ArrayList<Music> list = new ArrayList<Music>();
	private PlaylistDetailAdapter dadapter;

	private PlaylistAdapter adapter;
	private ArrayList<Playlist> plist = new ArrayList<Playlist>();
	ViewSwitcher switcher;
	int playListID;
	ListView lv, dlv;
	// ����ָʾ��ǰ���б�true�����ھ����б���
	boolean viewFlag = false;

	AlertDialog.Builder builder;

	AddMusicReceiver receiver = new AddMusicReceiver();
	IntentFilter filter = new IntentFilter();

	AddPlayListReceiver pReceiver = new AddPlayListReceiver();
	IntentFilter pFilter = new IntentFilter();

	AlertDialog alert = null;
	private AdapterView.AdapterContextMenuInfo delMenuInfo;

	// ���Ʋ���
	GestureOverlayView gestureoverlay1, gestureoverlay2;
	GestureLibrary gesLib;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.play_list_main);
		switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		setupPlaylist();
		pFilter.addAction(PlayListHelper.INTENT_ACTION_ADD_PLAYLIST_SUCCESS);
		registerReceiver(pReceiver, pFilter);
		// ���ƴ���
		gesLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		gesLib.load();
		gestureoverlay1 = (GestureOverlayView) this
				.findViewById(R.id.gestures1);

		MyOnGesturePerformedListener gpl = new MyOnGesturePerformedListener();

		gestureoverlay1.addOnGesturePerformedListener(gpl);
		gestureoverlay2 = (GestureOverlayView) this
				.findViewById(R.id.gestures2);
		gestureoverlay2.addOnGesturePerformedListener(gpl);
	}

	// ���ƴ���
	class MyOnGesturePerformedListener implements OnGesturePerformedListener {

		
		public void onGesturePerformed(GestureOverlayView overlay,
				Gesture gesture) {
			// TODO Auto-generated method stub
			ArrayList<Prediction> predictions = gesLib.recognize(gesture);
			if (predictions.size() > 0) {
				Prediction prediction = predictions.get(0);
				if (prediction.name.equals("add") // ��add����
						&& prediction.score > 1.5) {// �û����ƺͶ���õ����ƿ��е��������Ƴ̶�
					if (overlay.getId() == R.id.gestures1) {// �����б�
						addPlayList(null);
					} else { // ��������
						addMusic(null);
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(pReceiver);
		super.onDestroy();
	}

	public void setupPlaylist() {
		plist = GetPlaylist.getPlaylist(this);
		if (adapter == null) {// ��ʼ��
			adapter = new PlaylistAdapter(this, 0, plist);
			lv = (ListView) findViewById(R.id.listView1);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(this);
			lv.setOnCreateContextMenuListener(MenuLis);
		}
	}

	public void refreshPlaylist() {
		plist.clear();
		plist = GetPlaylist.getPlaylist(this);
		adapter.notifyDataSetChanged();
	}

	public void setupPlaylistDetail(int playListID) {
		if (dadapter == null) {// ���û�г�ʼ�������ʼ��
			dadapter = new PlaylistDetailAdapter(this, 0, list);
			dlv = (ListView) findViewById(R.id.listView21);
			dlv.setEmptyView(findViewById(android.R.id.empty));
			dlv.setAdapter(dadapter);
			dlv.setOnItemClickListener(this);
		}
		list.clear();
		list = GetTracksByPlaylist.getTracksByPlaylist(this, playListID);
		dadapter.notifyDataSetChanged();

		dlv.setOnCreateContextMenuListener(MenuLis);

		this.playListID = playListID;
	}

	public void refreshPlaylistDetail(int playListID) {
		list = GetTracksByPlaylist.getTracksByPlaylist(this, playListID);
		dadapter.notifyDataSetChanged();
		this.playListID = playListID;
	}

	@Override
	public void onBackPressed() {
		if (viewFlag) {
			viewFlag = false;
			switcher.showPrevious();
			this.unregisterReceiver(receiver);
		} else {
			AppExit.exitApp(this);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		refreshPlaylist();
		super.onResume();
	}

	class PlaylistAdapter extends ArrayAdapter<Playlist> {

		public PlaylistAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}

		public PlaylistAdapter(Context context, int textViewResourceId,
				List<Playlist> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return plist.get(position).get_id();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return plist.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = (RelativeLayout) inflater.inflate(
						R.layout.play_list_item, null);
				vh = new ViewHolder();
				vh.tv = (TextView) view.findViewById(R.id.textView1);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}
			vh.tv.setText(plist.get(position).getName());
			return view;
		}

		class ViewHolder {
			TextView tv;
		}
	}

	// ���ڲ��Ű�ť������
	public void now_playing(View view) {
		Intent intent = new Intent(this, PlayingActivity.class);
		PlayingHelper.fromActivity = PlayingActivity.class;
		startActivity(intent);
		// this.setContentView(R.layout.now_playing);
	}

	class PlaylistDetailAdapter extends ArrayAdapter<Music> {

		public PlaylistDetailAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public PlaylistDetailAdapter(Context context, int textViewResourceId,
				List<Music> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return list.get(position).getMusic_id();
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			// System.out.println("isEmpty?" + (getCount() == 0));
			return getCount() == 0;
		}

		@Override
		public int getCount() { // TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder vh;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = (RelativeLayout) inflater.inflate(
						R.layout.play_list_item, null);
				vh = new ViewHolder();
				vh.tv = (TextView) view.findViewById(R.id.textView1);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}

			vh.tv.setText(list.get(position).getTitle());
			return view;
		}

		class ViewHolder {
			TextView tv;
		}
	}

	class AddMusicReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// System.out.println("<><><><><><><><><"+playListID);
			// ���浽���ݿ⣬�ŵ�LV��
			int[] musics = arg1.getIntArrayExtra("musics");
			// System.out.println(list.size());
			AddMusicToPlaylist.addMusics(musics, TabPlayListActivity.this,
					playListID);
			refreshPlaylistDetail(playListID);
		}

	}

	class AddPlayListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			int id = intent.getIntExtra("playlist_id", 0);
			if (id != -1) {
				String name = intent.getStringExtra("playlist_name");
				Playlist pl = new Playlist();
				pl.set_id(id);
				pl.setName(name);
				plist.add(pl);
				adapter.notifyDataSetChanged();
				Toast.makeText(TabPlayListActivity.this, "���������б�ɹ�",
						Toast.LENGTH_LONG).show();
			} else {

				Toast.makeText(TabPlayListActivity.this, "���������б�ʧ��",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// ��������
	public void addMusic(View view) {
		Intent addIntent = new Intent(this, AddMusic2PlaylistActivity.class);
		startActivity(addIntent);
	}

	// ���������б�
	public void addPlayList(View view) {
		Intent addIntent = new Intent(this, AddPlaylistActivity.class);
		startActivity(addIntent);
	}

	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// �����б�
		if (parent == lv) {
			this.setupPlaylistDetail(plist.get(position).get_id());
			switcher.showNext();
			viewFlag = true;
			filter.addAction(PlayListHelper.INTENT_ACTION_ADD_MUSIC_PLAYLIST);
			this.registerReceiver(receiver, filter);
		} else if (parent == dlv) {// ��ϸ�б�
			PlayingHelper.playMusic(this, position, list);
		}
	}

	// ѡ�в˵�Item�󴥷�
	public boolean onContextItemSelected(MenuItem item) {
		// �ؼ�����������
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		if (viewFlag) {// ɾ���б��еĸ���
			if (DeleteMusicFromPlaylist.deleteMusic(this, playListID,
					list.get(menuInfo.position).getId_in_playlist()) > 0) {
				list.remove(menuInfo.position);
				dadapter.notifyDataSetChanged();
				Toast.makeText(this, "ɾ�������ɹ�", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "ɾ������ʧ��", Toast.LENGTH_LONG).show();
			}
		} else { // �����б����
			if (item.getItemId() == Menu.FIRST + 10) { // ɾ���б�
				showDeleteConfirmDialog(menuInfo);
			}
			if (item.getItemId() == Menu.FIRST + 11) { // �༭�б�
				Intent updateIntent = new Intent(this,
						UpdatePlaylistActivity.class);
				updateIntent.putExtra("playlist_id",
						plist.get(menuInfo.position).get_id());
				updateIntent.putExtra("playlist_name",
						plist.get(menuInfo.position).getName());
				startActivity(updateIntent);
			}
		}
		return super.onContextItemSelected(item);
	}

	private void deletePlaylist(AdapterView.AdapterContextMenuInfo menuInfo) {
		int i = PlaylistManager.deletePlaylist(this,
				plist.get(menuInfo.position).get_id());
		if (i > 0) {
			plist.remove(menuInfo.position);
			adapter.notifyDataSetChanged();
			Toast.makeText(this, "ɾ���б�ɹ�", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "ɾ���б�ʧ��", Toast.LENGTH_SHORT).show();
		}
	}

	// ɾ���б�ȷ�϶Ի���
	private void showDeleteConfirmDialog(
			AdapterView.AdapterContextMenuInfo menuInfo) {
		delMenuInfo = menuInfo;
		if (alert == null) {
			builder = new AlertDialog.Builder(this);
			builder.setMessage("��ȷ��ɾ�����б�ô��")
					.setCancelable(false)
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									deletePlaylist(delMenuInfo);

								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			alert = builder.create();
		}
		if (alert != null)
			alert.show();
	}

	ListView.OnCreateContextMenuListener MenuLis = new ListView.OnCreateContextMenuListener() {
		
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// ��Ӳ˵���
			if (viewFlag) {
				menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "���б���ɾ������");
				menu.setHeaderIcon(R.drawable.menu_edit);
				menu.setHeaderTitle("�༭����");
			} else {
				menu.add(Menu.NONE, Menu.FIRST + 10, Menu.FIRST + 10, "ɾ���б�");
				menu.add(Menu.NONE, Menu.FIRST + 11, Menu.FIRST + 11, "�༭�б�����");
				menu.setHeaderIcon(R.drawable.menu_edit);
				menu.setHeaderTitle("�༭�б�");
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (viewFlag)
			menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "��������").setIcon(
					R.drawable.addlist);
		else
			menu.add(Menu.NONE, Menu.FIRST + 10, Menu.FIRST, "�����б�").setIcon(
					R.drawable.addlist);

		// true-��ʾ�˵�
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case Menu.FIRST: // �����б��еĸ���
			// addIntent = new Intent(this, AddMusic2PlaylistActivity.class);
			// startActivity(addIntent);
			addMusic(null);
			break;
		case Menu.FIRST + 10:// �����б�
			// addIntent = new Intent(this, AddPlaylistActivity.class);
			// startActivity(addIntent);
			addPlayList(null);
			break;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear(); // ע���������Ҫ�ȵ���
		if (viewFlag)
			menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "��������").setIcon(
					R.drawable.addlist);
		else
			menu.add(Menu.NONE, Menu.FIRST + 10, Menu.FIRST, "�����б�").setIcon(
					R.drawable.addlist);
		return true;
	}

	// ͷ�����ظ����б�ť����
	public void back_to_playlist(View view) {
		switcher.showPrevious();
		viewFlag = false;
		this.unregisterReceiver(receiver);
	}
}
