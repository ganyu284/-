package cn.com.alex.imusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.alex.imusic.R;

import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.util.PlayerTimer;

public class PlayingActivityListViewAdapter extends ArrayAdapter<Music> {
	ArrayList<Music> list = new ArrayList<Music>();
	Context ctx;
	int idx;

	public PlayingActivityListViewAdapter(Context context,
			int textViewResourceId) {
		super(context, textViewResourceId);
		ctx = context;
	}

	public PlayingActivityListViewAdapter(Context context,
			int textViewResourceId, List<Music> objects) {
		super(context, textViewResourceId, objects);
		list = (ArrayList<Music>) objects;
		ctx = context;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	@Override
	public int getPosition(Music item) {
		// TODO Auto-generated method stub
		return list.indexOf(item);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Music getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.now_playing_item, null);

			vh = new ViewHolder();
			vh.tvTitle = (TextView) convertView.findViewById(R.id.song_title);
			vh.tvOrder = (TextView) convertView.findViewById(R.id.songOrder);
			vh.tvDuration = (TextView) convertView
					.findViewById(R.id.song_duration);
			vh.ivTri = (ImageView) convertView.findViewById(R.id.play_tri);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tvDuration.setText(PlayerTimer.format(list.get(position)
				.getDuration()));
		vh.tvTitle.setText(list.get(position).getTitle());
		int order = position + 1;
		vh.tvOrder.setText(order >= 100 ? "" + order : (order >= 10 ? "  "
				+ order : "    " + order));
		if (position == idx) {
			vh.ivTri.setVisibility(View.VISIBLE);
		} else {
			vh.ivTri.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvTitle, tvOrder, tvDuration;
		ImageView ivTri;
	}
}
