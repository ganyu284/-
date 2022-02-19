package cn.com.alex.imusic.bean;

public class Album {
	private int album_id;
	private String title;
	private String artist;
	private String album_art;// Cover
	private String first_year;
	private String last_year;
	private int num_of_songs;
	private int num_of_songs_for_artist;

	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum_art() {
		return album_art;
	}

	public void setAlbum_art(String album_art) {
		this.album_art = album_art;
	}

	public String getFirst_year() {
		return first_year;
	}

	public void setFirst_year(String first_year) {
		this.first_year = first_year;
	}

	public String getLast_year() {
		return last_year;
	}

	public void setLast_year(String last_year) {
		this.last_year = last_year;
	}

	public int getNum_of_songs() {
		return num_of_songs;
	}

	public void setNum_of_songs(int num_of_songs) {
		this.num_of_songs = num_of_songs;
	}

	public int getNum_of_songs_for_artist() {
		return num_of_songs_for_artist;
	}

	public void setNum_of_songs_for_artist(int num_of_songs_for_artist) {
		this.num_of_songs_for_artist = num_of_songs_for_artist;
	}

	public Album(int album_id, String title, String artist, String album_art) {
		super();
		this.album_id = album_id;
		this.title = title;
		this.artist = artist;
		this.album_art = album_art;
	}

	public Album(int album_id, String title, String artist) {
		super();
		this.album_id = album_id;
		this.title = title;
		this.artist = artist;
	}

	public Album(int album_id) {
		super();
		this.album_id = album_id;
	}

	public Album(int album_id, String title, String artist, String album_art,
			String first_year, int num_of_songs) {
		super();
		this.album_id = album_id;
		this.title = title;
		this.artist = artist;
		this.album_art = album_art;
		this.first_year = first_year;
		this.num_of_songs = num_of_songs;
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
		Album album = (Album) o;
		if (album.getAlbum_id() == this.getAlbum_id()) {
			return true;
		} else {
			return false;
		}
	}
}
