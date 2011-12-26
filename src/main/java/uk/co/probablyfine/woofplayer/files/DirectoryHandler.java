package uk.co.probablyfine.woofplayer.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public DirectoryHandler() {
		
	}
	
	public void loadFiles(File file) {
		log.debug("loadFiles - Loading files from {}");
		
		fileList = new ArrayList<File>();

		for (File i : Arrays.asList(file.listFiles())) {
		
			if (file.isDirectory()) {
				log.debug("loadFiles - Directory found, processing");
				loadFiles(i);
			
			} else if (file.isFile()) {
				log.debug("loadFiles - Single file found");
				fileList.add(i);
				
			} else {
				log.error("loadFiles - Unable to read directory/file - {}");
				continue;
			}
		}
		

	}
	
	public Collection<MusicFile> getPlaylist() {
		
		//We only want media files
		Predicate<File> mfPred = new Predicate<File>() {
			private String regex = ".*?\\.[mp3|mp4|flac|ogg]";
			public boolean apply(File file) {
				return file.getAbsoluteFile().toString().matches(regex);
			}
			
		};
		
		//Turn the remaining files into MusicFile with metadata
		Function<File, MusicFile> func = new Function<File, MusicFile>() {

			public MusicFile apply(File file) {
				return tag(file);
				
			}
		};
		
		return Collections2.transform(Collections2.filter(fileList, mfPred), func);
		
	}
	
	public MusicFile tag(File file) {
		
		MusicFile mf = new MusicFile();
		mf.setFile(file);
		
		Tag tag;
		
		try {
			
			tag = AudioFileIO.read(file).getTag();
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		Map<String,String> metadata = new HashMap<String, String>();
		
		metadata.put("location", file.getAbsolutePath());
		metadata.put("artist", tag.getFirst(FieldKey.ARTIST));
		metadata.put("album", tag.getFirst(FieldKey.ALBUM));
		metadata.put("title", tag.getFirst(FieldKey.TITLE));
		
		mf.setMetaData(metadata);
		
		return mf;
		
	}
	
	
}
