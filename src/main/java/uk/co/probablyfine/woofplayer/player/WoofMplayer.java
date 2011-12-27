package uk.co.probablyfine.woofplayer.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.probablyfine.woofplayer.files.MusicFile;
import uk.co.probablyfine.woofplayer.queue.MusicQueue;

import com.google.inject.Inject;

public class WoofMplayer implements MusicPlayer {

	private MusicQueue queue;
	private Thread playerThread;
	private Logger log = LoggerFactory.getLogger(WoofMplayer.class);
	
	@Inject
	public WoofMplayer(MusicQueue queue) {
		this.queue = queue;
	}
	
	public void play() {
		while (true) {
		
		final MusicFile file = queue.getNextSong();
		
		playerThread = new Thread(new Runnable() {
				
			private Process p;
			public void run() {
					try {
						p = Runtime.getRuntime().exec(new String[] {"mplayer", file.getFile().getAbsolutePath()});
					} catch (IOException e) {
						log.error("play - Cannot play current file, skipping");
						log.error("play - Exception : {}", e);
						return;
					}
					
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					String s;
					
		            try {
						while ((s = input.readLine()) != null) {
						    log.info("playTrack - {}", s);
					}
						
					} catch (IOException e) {
						log.error("playTrack - Error logging from {}",file.getMetaData().get("location"));
						log.error("playTrack - Exception: {}",e);
						throw new RuntimeException(e);
					}

		            try {
						while ((s = error.readLine()) != null) {
						   	log.error("playTrack - {}", s);
						}
					} catch (IOException e) {
						log.error("playTrack - Error logging error from {}",file.getMetaData().get("location"));
						log.error("playTrack - Exception: {}",e);
						throw new RuntimeException(e);
					}
					
				}
			}); 
			
			playerThread.start();
			try {
				playerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void skipSong() {
		log.debug("skipSong - Skipping current song");
		playerThread.interrupt();
		playerThread = null;
	}

}
