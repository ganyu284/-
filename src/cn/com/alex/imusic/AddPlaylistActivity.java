package cn.com.alex.imusic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.alex.imusic.getdata.PlaylistManager;

public class AddPlaylistActivity extends Activity {
	EditText et;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplaylist);
		et = (EditText) findViewById(R.id.editText1);
		title = (TextView)findViewById(R.id.title);
		title.setText("新增列表");
	}

	public void saveList(View view) {
		String playlistname = et.getText().toString().trim();
		if(playlistname.length()==0){
			Toast.makeText(this, "请输入列表名称。", Toast.LENGTH_LONG).show();
			return;
		}
		PlaylistManager.addPlaylist(this, playlistname);
		finish();
	}

	public void cancel(View view) {
		finish();
	}
}
