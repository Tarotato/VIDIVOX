package background_tasks;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import commentary_manipulation.PleaseWaitDialog;

import uk.co.caprica.vlcj.player.MediaPlayer;
import video_manipulation.ProgressDisplayPanel;
import vidivox_beta.HelperFile;
import vidivox_beta.MainFrame;

/** 
 * @author Isabel Zhuang
 * Class merges a selected audio file (mp3) with the current video in a back ground task
 */
public class BgMergeFiles extends SwingWorker<Void, Void> {	
	private String videoPath;
	private String mp3Path;
	private PleaseWaitDialog waitDialog;
	private JFrame thisFrame;
	private MediaPlayer video;	
	
	public BgMergeFiles(String videoPath, String mp3Path, PleaseWaitDialog waitDialog, JFrame thisFrame, MediaPlayer video) {
		this.videoPath = videoPath;
		this.mp3Path = mp3Path;
		this.waitDialog = waitDialog;
		this.thisFrame = thisFrame;
		this.video = video;
	}	
	@Override
	protected Void doInBackground() throws Exception {
		
		waitDialog.setModal(true);
		
		// Deletes any existing vidAudio.mp3 file
		String cmd = "rm -r ./MP3Files/.vidAudio.mp3";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process = builder.start();
		process.waitFor(); // Stops thread from continuing so previous command can finish executing
		
		// Creates an mp3 file from the existing video's audio
		cmd = "ffmpeg -i " + videoPath + " -map 0:1 ./MP3Files/.vidAudio.mp3";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor();
		
		// Creates an mp3 file output.mp3 that combines the video audio and selected mp3 audio for merging		
		if(ProgressDisplayPanel.silenceTime>0){
			// Creates an audio file with a silence at the start if insert time specified
			System.out.println(ProgressDisplayPanel.silenceTime+"addsilence");
			int silenceLength = ProgressDisplayPanel.silenceTime;
			//Creates a silent wav file
			cmd = "ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0:0::duration="+silenceLength+" ./MP3Files/.silence.wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();
			process.waitFor();
			
			// Concatenates the silent wav file with the mp3 file
			cmd = "ffmpeg -y -i ./MP3Files/.silence.wav -i "+mp3Path+" -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume=1[out]' -map '[out]' './MP3Files/.output1.mp3'";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();				
			process.waitFor();
			
			cmd = "ffmpeg -y -i './MP3Files/.output1.mp3' -i './MP3Files/.vidAudio.mp3' -filter_complex amix=inputs=2 './MP3Files/.output.mp3'"; 
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();
			process.waitFor();
		}else{
			// Else create an mp3 that merges the mp3 file at the start
			cmd = "ffmpeg -y -i " + mp3Path + " -i ./MP3Files/.vidAudio.mp3 -filter_complex amix=inputs=2 ./MP3Files/.output.mp3"; 
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();
			process.waitFor();
		}
		
		// Creates an output.avi file from merging combined audio (0:0) and existing video stream (1:0)
		cmd = "ffmpeg -i "+ videoPath + " -i ./MP3Files/.output.mp3 -map 0:0 -map 1:0 -acodec copy -vcodec copy ./VideoFiles/"+MainFrame.videoName[0]+".avi";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor(); 

		return null;
	}
	
	@Override
	protected void done(){
		// Close window with progress bar
		waitDialog.dispose();
		
		//Check if file has been created
		if (HelperFile.isGenerated(MainFrame.videoName[0])) {
			int n = JOptionPane.showConfirmDialog(thisFrame,"Successfully merged " + HelperFile.getBasename(mp3Path) + " with "
							+ HelperFile.getBasename(videoPath) + ".\n Would you like to play it now?", null, JOptionPane.OK_CANCEL_OPTION);
			if (n == 0) { 
				// Change the video to output.avi if user selects "OK"
				video.playMedia("VideoFiles/" + MainFrame.videoName[0] + ".avi");
				HelperFile.setCurrentVideoPath(System.getProperty("user.dir") + "VideoFiles/" + MainFrame.videoName[0] + ".avi");
				// Set button to play if video is paused
				if (!MainFrame.playClicked) {
					MainFrame.setToPlay();
				}
			}
		}else{
			// Show error if file is not created
			JOptionPane.showMessageDialog(thisFrame, "Video creation failed: Please try again", "Error" , JOptionPane.ERROR_MESSAGE);
		}		
	}
}




