package uk.co.probablyfine.woofplayer.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;


public class DirectoryHandler {

	private final Logger log = LoggerFactory.getLogger(DirectoryHandler.class);
	private List<File> fileList;
	
	@Inject
	public DirectoryHandler() {}
	
	public void loadFiles(File file) {
		log.debug("loadFiles - Loading files from {}");
		
		fileList = new ArrayList<File>();

		for (File i : Arrays.asList(file.listFiles())) {
			System.out.println(i.getAbsolutePath());
			if (i.isDirectory()) {
				log.debug("loadFiles - Directory found, processing");
				loadFiles(i);
				
			} else if (i.isFile()) {
				log.debug("loadFiles - Single file found");
				fileList.add(i);
			} else {
				log.error("loadFiles - Unable to read directory/file - {}");
				continue;
			}
		}
		

	}
	
	public Collection<MusicFile> getPlaylist() {
		
		final String regex = ".*\\.(mp3|mp4|flac|ogg)$";
		
		//We only want media files
		Predicate<File> mfPred = new Predicate<File>() {
			
			public boolean apply(File file) {
				return Pattern.matches(regex, file.getAbsolutePath());
			}
		};
		
		//Turn the remaining files into MusicFile with metadata
		Function<File, MusicFile> func = new Function<File, MusicFile>() {
			public MusicFile apply(File file) {
				return tag(file);
			}
		};
		
		//Turn off jaudiotagger's logging.
		java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
		
		//Filter away non-music files, then turn them into MusicFiles
		return Collections2.transform(Collections2.filter(fileList, mfPred), func);
	}
	
	public MusicFile tag(File file) {
		
		MusicFile mf = new MusicFile();
		mf.setFile(file);
		
		Tag tag;
		Map<String,String> metadata = new HashMap<String, String>();
		
		try {
			tag = AudioFileIO.read(file).getTag();
			
			metadata.put("location", file.getAbsolutePath());
			metadata.put("artist", tag.getFirst(FieldKey.ARTIST));
			metadata.put("album", tag.getFirst(FieldKey.ALBUM));
			metadata.put("title", tag.getFirst(FieldKey.TITLE));
			
		} catch (CannotReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			e.printStackTrace();
		}
		
		mf.setMetaData(metadata);
		
		return mf;
		
	}
	
}
