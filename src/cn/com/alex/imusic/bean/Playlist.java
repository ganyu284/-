package cn.com.alex.imusic.bean;
//还要实现comparable接口
public class Playlist {
	int _id;
	String name;
	long date_added;
	long date_modified;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate_added() {
		return date_added;
	}

	public void setDate_added(long date_added) {
		this.date_added = date_added;
	}

	public long getDate_modified() {
		return date_modified;
	}

	public void setDate_modified(long date_modified) {
		this.date_modified = date_modified;
	}
}
