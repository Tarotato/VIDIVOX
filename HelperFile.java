package vidivox_beta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import uk.co.caprica.vlcj.player.MediaPlayer;


/**
 * @author Isabel Zhuang
 * Class contains necessary methods for completing actions in MainFrame
 */
public class HelperFile {

	private static ProcessBuilder builder;
	private static Process process;
//	protected static PleaseWaitDialog waitdialog;
	
	/**
	 * Checks if the chosen file path from JFileChooser is a video (avi/mpeg4)
	 * @param path
	 * @return
	 */
	protected static boolean isVideo(String path) {
		
		String cmd = "file "+ path;
		
		// Determines if the file path chosen is a video file
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process;
		try {
			process = processBuilder.start();
			InputStream output = process.getInputStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(output));

			String line = null;
			while ((line = stdout.readLine()) != null) {
				if (line.matches("(.*)AVI(.*)")){ // Matches AVI format
					return true;
				}
				if (line.matches("(.*)ISO Media, MPEG v4 system(.*)")){ // Matches MP4 format
					return true;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		return false;
	}
	
	/**
	 * Checks if the chosen file path from JFileChooser is an mp3/audio file
	 * @param path
	 * @return
	 */
	protected static boolean isMp3(String path) {
		
	    String cmd = "file "+ path;

		//Determine if file chosen is a audio file
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process;
		try {
			process = processBuilder.start();
			InputStream output = process.getInputStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(output));

			String line = null;
			while ((line = stdout.readLine()) != null) {
				if (line.matches("(.*): Audio file(.*)")){ // Matches audio format
					return true;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		return false;		
	}

	//Unused Method
/*	*//**
	 * Uses BASH commands to generate an output video merged with the selected mp3 file 
	 * @param mp3Path
	 * @param videoPath
	 *//*
	protected static void mergeMp3(String mp3Path, String videoPath, JFrame thisFrame, String[] videoName) {
		try {
			JOptionPane.showMessageDialog(thisFrame, "Your video will start merging as soon as you click OK, please be patient while it merges.");

			
			waitdialog = new PleaseWaitDialog();
			BgMergeFiles mergeFiles = new BgMergeFiles(videoPath, mp3Path, waitdialog);
			mergeFiles.execute();
				
			waitdialog.setModal(true);
			waitdialog.setVisible(true);
					
			// Deletes any existing output.mp3 file
			String cmd = "rm -r ./MP3Files/.vidAudio.mp3";
			startProcess(cmd);			
			process.waitFor(); // Stops thread from continuing so previous command can finish executing
			
			// Creates an mp3 file from the existing video's audio
			cmd = "ffmpeg -i " + videoPath + " -map 0:1 ./MP3Files/.vidAudio.mp3";
			startProcess(cmd);			
			process.waitFor();
			
			// Deletes any existing output.mp3 file
			cmd = "rm -r ./MP3Files/.output.mp3";
			startProcess(cmd);			
			process.waitFor();
			
			// Creates an mp3 file output.mp3 that combines the video audio and selected mp3 audio for merging 
			cmd = "ffmpeg -i " + mp3Path + " -i ./MP3Files/.vidAudio.mp3 -filter_complex amix=inputs=2 ./MP3Files/.output.mp3";
			startProcess(cmd);			
			process.waitFor();
			
			// Creates an output.avi file from merging combined audio (0:0) and existing video stream (1:0)
			cmd = "ffmpeg -i ./MP3Files/.output.mp3 -i "+ videoPath + " -map 0:0 -map 1:0 -acodec copy -vcodec copy ./VideoFiles/"+MainFrame.videoName[0]+".avi";
			startProcess(cmd);			
			process.waitFor(); 
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}					
	}*/
	
	/**
	 * Generates an mp3 file from the written commentary (text)
	 * @param commentary
	 * @param name
	 * @param thisDialog
	 */
	protected static void saveAsMp3(String commentary, String name, JDialog thisDialog) {
		if (commentary != null) {
	   		
			try {
				String textPath = System.getProperty("user.dir")+ "/.commentary.txt"; // Generate a hidden .txt file containing user commentary
				BufferedWriter bw = new BufferedWriter(new FileWriter(textPath, false));
				bw.write(commentary);
				bw.close();
						
				// Generate a hidden sound.wav file from the saved user commentary
				String cmd = "text2wave " + textPath + " -o ./MP3Files/.sound.wav";
				startProcess(cmd);		
				process.waitFor();
				
				// Find out if any .mp3 file in MP3Files folder has the same name user has entered for MP3 file name
				cmd = "find | grep -x \"./MP3Files/" + name +".mp3\" | wc -l";
				startProcess(cmd);
				
				builder.redirectErrorStream(true);
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
						
				String line = stdoutBuffered.readLine();								
						
				// Generate an .mp3 file if none already exists
				if(line.equals("0")) {
					// Adds period of silence to the beginning of the mp3 if not inserted at beginning of video
					if(ProgressDisplayPanel.silenceTime>0){
						addSilence(ProgressDisplayPanel.silenceTime, name);
					}else{
						// Creates an mp3 without a period of silence
						cmd = "ffmpeg -i ./MP3Files/.sound.wav \'./MP3Files/" + name + ".mp3\'";
						startProcess(cmd);
					}							
					JOptionPane.showMessageDialog(thisDialog, "Successfully saved "+ name +".mp3 to MP3Files");
					thisDialog.dispose();						
				} else {
					// Error dialog if name of mp3 already exists
					JOptionPane.showMessageDialog(thisDialog, "This name is taken. Please choose another.", null , JOptionPane.INFORMATION_MESSAGE);
				}						
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 *  Concatenates silence at the start of a .wav file 
	 * @param silenceLength
	 * @throws InterruptedException 
	 */
	protected static void addSilence(int silenceLength, String name) throws InterruptedException{
		String cmd = "";		
		try{
			silenceLength = (silenceLength*2);
			//Creates a silent wav file
			cmd = "ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0:0::duration="+silenceLength+" ./MP3Files/.silence.wav";
			startProcess(cmd);
			
			// Concatenates the silent wav file with the mp3 file
			cmd = "ffmpeg -y -i ./MP3Files/.silence.wav -i ./MP3Files/.sound.wav -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume=1[out]' -map '[out]' './MP3Files/"+name+".mp3'";
			startProcess(cmd);			
			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the length of an audio file
	 * @param mp3Path
	 */
	protected static void getDuration(String mp3Path, MediaPlayer video, JFrame thisFrame){
		File file = new File(mp3Path);
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
			
		AudioFormat format = audioInputStream.getFormat();
		// Get necessary for calculation 
		long audioFileLength = file.length();
		int frameSize = format.getFrameSize();
		float frameRate = format.getFrameRate();
		float durationInSeconds = (audioFileLength / (frameSize * frameRate)); // Calculates the time of the audio file		
		
		if(durationInSeconds>=((video.getTime())/1000)/60){
			JOptionPane.showMessageDialog(thisFrame, "Please add video at a suitable time", "Error: Invalid Time" , JOptionPane.ERROR_MESSAGE);
		}
		
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Starts building a process for any BASH command passed in
	 * @param cmd
	 * @throws IOException
	 */
	private static void startProcess(String cmd) throws IOException{
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();		
	}
	
	/**
	 * Gets the basename of a file path and returns it as a string
	 * @param path
	 * @return
	 */
	protected static String getBasename(String path){
		int index = path.lastIndexOf('/');
		return path.substring(index+1); // Get string after index+1 (the basename)		
	}

	/**
	 * Sets the videoPath of current video
	 * @param newPath
	 */
	public static void setCurrentVideoPath(String newPath) {
		MainFrame.currentVideoPath = newPath;
	}
	
	/**
	 * Returns the videoPath of current video
	 * @return
	 */
	public static String getCurrentVideoPath() {
		return MainFrame.currentVideoPath;
	}
}

