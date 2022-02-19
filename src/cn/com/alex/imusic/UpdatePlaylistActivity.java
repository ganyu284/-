package cn.com.alex.imusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.alex.imusic.getdata.PlaylistManager;

public class UpdatePlaylistActivity extends Activity {
	EditText et;
	int playlist_id;
	String playlist_name;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplaylist);
		et = (EditText) findViewById(R.id.editText1);
		Intent intent = this.getIntent();
		playlist_id = intent.getIntExtra("playlist_id", -1);
		playlist_name = intent.getStringExtra("playlist_name");
		et.setText(playlist_name);
		
		title = (TextView)findViewById(R.id.title);
		title.setText("±à¼­ÁÐ±í");
	}

	public void saveList(View view) {
		String playlistname = et.getText().toString();
		PlaylistManager.updatePlaylist(this, playlist_id, playlistname);
		finish();
	}

	public void cancel(View view) {
		finish();
	}
}
