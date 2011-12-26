package uk.co.probablyfine.woofplayer.files;

import java.io.File;
import java.util.Map;

public class MusicFile {

	private File file;
	private Map<String,String> metaData;
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public Map<String, String> getMetaData() {
		return metaData;
	}
	
	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}
	
}
