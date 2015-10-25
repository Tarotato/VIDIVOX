package vidivox_beta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import commentary_manipulation.TextEditingPanel;

/**
 * @author Isabel Zhuang
 * Class contains necessary methods for completing actions in MainFrame
 */
public class HelperFile {
	private static ProcessBuilder builder;
	private static Process process;
	/**
	 * Checks if the chosen file path from JFileChooser is a video (avi/mpeg4)
	 * @param path
	 * @return
	 */
	public static boolean isVideo(String path) {		
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
	public static boolean isMp3(String path) {		
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
	
	/**
	 * Generates an mp3 file from the written commentary (text)
	 * @param commentary
	 * @param name
	 * @param thisDialog
	 */
	public static void saveAsMp3(String commentary, String name, JDialog thisDialog) {
		if (commentary != null) {	   		
			try {
				String textPath = System.getProperty("user.dir")+ "/.commentary.txt"; // Generate a hidden .txt file containing user commentary
				BufferedWriter bw = new BufferedWriter(new FileWriter(textPath, false));
				bw.write(commentary);
				bw.close();
				
				// Generate a hidden sound.wav file from the saved user commentary
				// Change the speed of the voice if a value is set			
				if (TextEditingPanel.voiceSpeed != 0){
					int voiceSpeed = (int) ((1 - TextEditingPanel.voiceSpeed*.25)*100) ;					
					String scm = "text2wave -o './MP3Files/.sound.wav' './.commentary.txt' -eval \'./.scmFiles/"+voiceSpeed+".scm\'";
					startProcess(scm);		
					process.waitFor();					
				}else{
					// Generate a hidden sound.wav file from the saved user commentary
					String cmd = "text2wave " + textPath + " -o ./MP3Files/.sound.wav";
					startProcess(cmd);		
					process.waitFor();
				}				
				// Find out if any .mp3 file in MP3Files folder has the same name user has entered for MP3 file name
				String cmd = "find | grep -x \"./MP3Files/" + name +".mp3\" | wc -l";
				startProcess(cmd);				
				builder.redirectErrorStream(true);
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
						
				String line = stdoutBuffered.readLine();					
				// Generate an .mp3 file if none already exists
				if(line.equals("0")) {					
						// Creates an mp3 if name is not taken
						cmd = "ffmpeg -i ./MP3Files/.sound.wav \'./MP3Files/" + name + ".mp3\'";
						startProcess(cmd);
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
	public static void addSilence(int silenceLength, String mp3Path) throws InterruptedException{	
		try{
			silenceLength = (silenceLength*2);
			//Creates a silent wav file
			String cmd = "ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0:0::duration="+silenceLength+" ./MP3Files/.silence.wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();	
			
			// Concatenates the silent wav file with the mp3 file
			cmd = "ffmpeg -y -i ./MP3Files/.silence.wav -i "+mp3Path+" -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume=1[out]' -map '[out]' './MP3Files/.ouput.mp3'";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();				
			process.waitFor();
		} catch (IOException e) {
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
	public static String getBasename(String path){
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
	
	/**
	 * Returns true if the file was successfully made
	 * @param fileName
	 * @return
	 */
	public static boolean isGenerated(String fileName){
		String cmd = "find | grep -x \"./VideoFiles/" + fileName +".avi\" | wc -l";
		try {		
			startProcess(cmd);				
			builder.redirectErrorStream(true);
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
					
			String line = stdoutBuffered.readLine();	
			System.out.println(line);
			if (line.equals("0")) {
				return false;
			} else {
				return true;
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

