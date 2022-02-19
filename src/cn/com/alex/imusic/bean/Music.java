package cn.com.alex.imusic.bean;
//还要实现comparable接口
import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1552573222705989029L;
	private int music_id;
	private int album_id;
	private int artist_id;
	private int title_key;
	private int id_in_playlist;// 在PlayList中的ID，不一定有

	private String album;
	private String artist;
	private String genres;
	private String lyrics;
	private String cover;
	private int duration;
	private String url;
	private String title;
	private String data;
	public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
		public Music createFromParcel(Parcel in) {
			return new Music(in);
		}

		public Music[] newArray(int size) {
			return new Music[size];
		}
	};

	public Music(Parcel in) {
		readFromParcel(in);
	}

	public Music() {
		super();
	}

	public Music(int music_id) {
		super();
		this.music_id = music_id;
	}

	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(music_id);
		dest.writeInt(artist_id);
		dest.writeInt(album_id);
		dest.writeInt(duration);
		dest.writeInt(title_key);
		dest.writeString(album);
		dest.writeString(artist);
		dest.writeString(cover);
		dest.writeString(data);
		dest.writeString(genres);
		dest.writeString(lyrics);
		dest.writeString(url);
		dest.writeString(title);

	}

	public void readFromParcel(Parcel in) {
		music_id = in.readInt();
		artist_id = in.readInt();
		album_id = in.readInt();
		duration = in.readInt();
		title_key = in.readInt();
		album = in.readString();
		artist = in.readString();
		cover = in.readString();
		data = in.readString();
		genres = in.readString();
		lyrics = in.readString();
		url = in.readString();
		title = in.readString();
	}

	public int getTitle_key() {
		return title_key;
	}

	public void setTitle_key(int title_key) {
		this.title_key = title_key;
	}

	public int getMusic_id() {
		return music_id;
	}

	public void setMusic_id(int music_id) {
		this.music_id = music_id;
	}

	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setArtist_id(int artist_id) {
		this.artist_id = artist_id;
	}

	public int getArtist_id() {
		return artist_id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbum() {
		return album;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub

		if (o == null)
			return false;
		if (o.getClass() != this.getClass()) {
			return false;
		}
		if (o == this) {
			return true;
		}
		Music music = (Music) o;
		if (music.getMusic_id() == this.getMusic_id()) {
			return true;
		} else {
			return false;
		}
	}

	public void setId_in_playlist(int id_in_playlist) {
		this.id_in_playlist = id_in_playlist;
	}

	public int getId_in_playlist() {
		return id_in_playlist;
	}

}
