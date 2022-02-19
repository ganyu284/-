package cn.com.alex.imusic.bean;

public class Artist {
	private int _id;
	private String artist;
	
	private int num_of_tracks;
	private int num_of_albums;

	public Artist(int _id, String artist) {
		super();
		this._id = _id;
		this.artist = artist;
	}

	public Artist(int _id, String artist, int num_of_tracks, int num_of_albums) {
		super();
		this._id = _id;
		this.artist = artist;
		this.num_of_tracks = num_of_tracks;
		this.num_of_albums = num_of_albums;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setNum_of_tracks(int num_of_tracks) {
		this.num_of_tracks = num_of_tracks;
	}

	public int getNum_of_tracks() {
		return num_of_tracks;
	}

	public void setNum_of_albums(int num_of_albums) {
		this.num_of_albums = num_of_albums;
	}

	public int getNum_of_albums() {
		return num_of_albums;
	}


}
