package uk.co.probablyfine.woofplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.probablyfine.woofplayer.files.DirectoryHandler;
import uk.co.probablyfine.woofplayer.player.MusicPlayer;
import uk.co.probablyfine.woofplayer.queue.MusicQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WoofPlayer {

	private final List<Option> optionList;
	private final Logger log = LoggerFactory.getLogger(WoofPlayer.class);
	private String directory;
	
	public static boolean verbose = false;
	public static boolean shuffle = false;
		
	public static void main(String[] args) {
		
		WoofPlayer woof = new WoofPlayer();
		woof.processArgs(args);
		woof.startPlayer();
		
	}

	private void startPlayer() {
		Injector injector = Guice.createInjector(new WoofPlayerModule());
		DirectoryHandler handler = injector.getInstance(DirectoryHandler.class);
		
		File file = new File(directory);
		
		log.info("startPlayer - {}",file.getAbsoluteFile());
		
		try {
			handler.loadFiles(file);
		} catch (RuntimeException e) {
			log.error("startPlayer - Unable to load files");
			log.error("startPlayer - Exception: {}",e);
			System.err.println("Unable to load files, exiting!");
			return;
		}
		
		MusicQueue queue = injector.getInstance(MusicQueue.class);
		queue.queueFiles(handler.getPlaylist());
		
		MusicPlayer player = injector.getInstance(MusicPlayer.class);
		player.play();
		
	}

	private void processArgs(String[] args) {
		
		Options options = new Options();
		
		for(Option o : optionList) {
			options.addOption(o);
		}
		
		CommandLine parser;
		
		try {
			parser = new GnuParser().parse(options, args);
		} catch (ParseException e) {
			log.error("processArgs - Unable to create parser with options");
			log.error("processArgs - Exception: {}",e);
			System.out.println("Unable to start Woofplayer, check error log for details.");
			return;
		}	
		
		if (parser.hasOption("directory")) {
			directory = parser.getOptionValue("directory");
			log.debug("processArgs - Directory param given, setting to {}",directory);
		} else {
			directory = System.getProperty("user.dir");
			log.debug("processArgs - Directory param not given, setting to {}",directory);
		}

		if (parser.hasOption("shuffle")) {
			log.debug("processArgs - Shuffle on");
			WoofPlayer.shuffle = true;
		}
		
		if (parser.hasOption("verbose")) {
			log.debug("processArgs - Verbose on");
			WoofPlayer.verbose = true;
		}
		
		if (parser.hasOption("help")) {
			new HelpFormatter().printHelp("woofplayer", "\na simple java media player for music directories", options, "\nWoof! :3");
			System.exit(0);
		}
		
	}
	
	@SuppressWarnings("static-access")
	private WoofPlayer() {
		optionList = new ArrayList<Option>();
		
		optionList.add(OptionBuilder
				.withLongOpt("directory")
				.withDescription("directory to play media from")
				.withArgName("dir")
				.hasArg()
				.create("d"));
		
		optionList.add(new Option("s", "shuffle", false, "shuffle the music"));
		optionList.add(new Option("h", "help", false, "display this message"));
	}
	
}
