package uk.co.probablyfine.woofplayer;

import uk.co.probablyfine.woofplayer.files.DirectoryHandler;
import uk.co.probablyfine.woofplayer.player.MusicPlayer;
import uk.co.probablyfine.woofplayer.player.WoofMplayer;
import uk.co.probablyfine.woofplayer.queue.MusicQueue;
import uk.co.probablyfine.woofplayer.queue.WoofQueue;

import com.google.inject.AbstractModule;

public class WoofPlayerModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(MusicPlayer.class).to(WoofMplayer.class);
		bind(MusicQueue.class).to(WoofQueue.class);
		bind(DirectoryHandler.class);
		
	}

}
