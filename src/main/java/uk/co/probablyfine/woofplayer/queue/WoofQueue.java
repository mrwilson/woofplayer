package uk.co.probablyfine.woofplayer.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.probablyfine.woofplayer.WoofPlayer;
import uk.co.probablyfine.woofplayer.files.MusicFile;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class WoofQueue implements MusicQueue {

	private final Queue<MusicFile> queue = new LinkedList<MusicFile>();
	
	private	final Logger log = LoggerFactory.getLogger(WoofQueue.class);
	
	@Inject
	public WoofQueue() {}
		
	public void queueFiles(Collection<MusicFile> mfColl) {
		
		log.debug("queueFiles - Constructing a queue of files");
		
		List<MusicFile> fileList = new ArrayList<MusicFile>(mfColl);
		
		if (WoofPlayer.shuffle) {
			log.debug("queueFiles - Shuffling files");
			Collections.shuffle(fileList);
		} 
		
		log.debug("queueFiles - Adding files to queue");
		queue.addAll(fileList);
		
	}
	
	public int filesLeft() {
		log.debug("filesLeft - Returning queue size");
		return queue.size();
	}

	public MusicFile getNextSong() {
		log.debug("getNextSong - Retrieving song from queue");
		MusicFile file = queue.remove();
		queue.add(file);
		return file;
	}

}
