package kr.userapps.indexextract.objects;

import com.google.gson.annotations.SerializedName;

public class Version {
	@SerializedName("id")
	public String id;
	
	@SerializedName("type")
	public VersionType type;
	
	@SerializedName("time")
	public String time;
	
	@SerializedName("releaseTime")
	public String rd;
	
	@SerializedName("url")
	public String url;
}
