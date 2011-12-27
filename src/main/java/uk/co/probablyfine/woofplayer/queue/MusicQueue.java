package uk.co.probablyfine.woofplayer.queue;

import java.util.Collection;

import uk.co.probablyfine.woofplayer.files.MusicFile;

public interface MusicQueue {

	public int filesLeft();
	
	public MusicFile getNextSong();

	public void queueFiles(Collection<MusicFile> mfColl);
	
}