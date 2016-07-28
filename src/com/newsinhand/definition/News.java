package com.newsinhand.definition;

public class News {
	private String _title = "";
	private String _link = "";
	private String _desc = "";
	private String _image = "";
	private String _date = "";
	private String _sort = "";
	private String _refresh = "";
	private String _store = "";
	private String _content="";
	public String get_content() {
		return _content;
	}

	public void set_content(String _content) {
		this._content = _content;
	}

	private int _id =0;

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getStore() {
		return _store;
	}

	public void setStore(String _store) {
		this._store = _store;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String _date) {
		this._date = _date;
	}

	public String getSort() {
		return _sort;
	}

	public void setSort(String _sort) {
		this._sort = _sort;
	}

	public String getRefresh() {
		return _refresh;
	}

	public void setRefresh(String _refresh) {
		this._refresh = _refresh;
	}

	public String getTitle() {
		return _title;
	}

	public String getLink() {
		return _link;
	}

	public String getDesc() {
		return _desc;
	}

	public String getImage() {
		return _image;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setLink(String link) {
		_link = link;
	}

	public void setDesc(String desc) {
		_desc = desc;
	}

	public void setImage(String image) {
		_image = image;
	}
}
