package uk.co.probablyfine.woofplayer.queue;

import java.util.Collection;

public interface MusicQueue<E> {

	public int filesLeft();
	
	public E getNextSong();

	public void queueFiles(Collection<E> mfColl);
	
}