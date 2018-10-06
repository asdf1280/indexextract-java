package indexextract.objects;

public enum VersionType {
	release, snapshot, old_beta, old_alpha;
	
	public int index() {
		int n = 0;
		if(this == VersionType.snapshot) n = 1;
		else if(this == old_beta) n = 2;
		else if(this == old_alpha) n = 3;
		return n;
	}
	
}
