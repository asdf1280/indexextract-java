package indexextract.objects;

import com.google.gson.annotations.SerializedName;

public class AssetObject {
	@SerializedName("hash")
	public String hash;
	@SerializedName("size")
	public int size;
}
