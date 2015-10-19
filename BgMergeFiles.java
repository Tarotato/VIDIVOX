package vidivox_beta;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.MediaPlayer;

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
		
		// Deletes any existing output.mp3 file
		String cmd = "rm -r ./MP3Files/.vidAudio.mp3";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process = builder.start();
		process.waitFor(); // Stops thread from continuing so previous command can finish executing
		
		// Creates an mp3 file from the existing video's audio
		cmd = "ffmpeg -i " + videoPath + " -map 0:1 ./MP3Files/.vidAudio.mp3";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor();
		
		// Deletes any existing output.mp3 file
		cmd = "rm -r ./MP3Files/.output.mp3";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor();
		
		// Creates an mp3 file output.mp3 that combines the video audio and selected mp3 audio for merging 
		cmd = "ffmpeg -i " + mp3Path + " -i ./MP3Files/.vidAudio.mp3 -filter_complex amix=inputs=2 ./MP3Files/.output.mp3"; 
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor();
		
		// Creates an output.avi file from merging combined audio (0:0) and existing video stream (1:0)
		cmd = "ffmpeg -i ./MP3Files/.output.mp3 -i "+ videoPath + " -map 0:0 -map 1:0 -acodec copy -vcodec copy ./VideoFiles/"+MainFrame.videoName[0]+".avi";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor(); 

		return null;
	}
	
	@Override
	protected void done(){
		//get rid of progress bar
		waitDialog.dispose();
		int n = JOptionPane.showConfirmDialog(thisFrame, "Successfully merged "+ HelperFile.getBasename(mp3Path) +" with "+ HelperFile.getBasename(videoPath) +".\n Would you like to play it now?", null, JOptionPane.OK_CANCEL_OPTION);
		
		if(n == 0) { // Change the video to output.avi if user selects "OK"
			video.playMedia("VideoFiles/"+MainFrame.videoName[0]+".avi");
			HelperFile.setCurrentVideoPath(System.getProperty("user.dir") + "VideoFiles/"+MainFrame.videoName[0]+".avi");
		}	
		
	}
}




