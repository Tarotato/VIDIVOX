package VIDIVOX_prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class CustomMenuBar extends JMenuBar{
	
	public CustomMenuBar(final MediaPlayer video, final MainFrame mainFrame){
		
		JMenu mnFile = new JMenu("File");
		this.add(mnFile);
		
		JMenuItem mntmOpenNewVideo = new JMenuItem("Open New Video...");
		mntmOpenNewVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Changing the video that we are editing
				// Prompt user for the video they want to change to
				String newPath;
				JFileChooser videoChooser = new JFileChooser(System.getProperty("user.dir") + "/VideoFiles/");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Video File", "avi");
				videoChooser.setFileFilter(filter);
				int okReturnVal = videoChooser.showOpenDialog(getParent());
				if(okReturnVal == JFileChooser.APPROVE_OPTION) {
					newPath = videoChooser.getSelectedFile().getPath();
					// Check if file chosen is a video, if yes, change video, if not, show error dialog and do nothing
					if(VideoMethods.isVideo(newPath)) {
						video.playMedia(newPath);
						VideoMethods.setCurrentVideoPath(newPath);
					} else {
						JOptionPane.showMessageDialog(mainFrame, "The file you have chosen is not a video, please try again.");
					}
				}
				
			}
		});
		mnFile.add(mntmOpenNewVideo);
	}

}
