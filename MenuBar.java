package video_manipulation;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.player.MediaPlayer;
import vidivox_beta.HelperFile;
import vidivox_beta.MainFrame;

/**
 * @author Isabel Zhuang
 * Class creates the menu bar with custom functions  
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar{	
	public MenuBar(final MediaPlayer video, final MainFrame mainFrame, final JSplitPane splitPane, final int[] vidLength, final JProgressBar bar, final JPanel videoPanel){
		
		// Lets user open a new video file
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
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
					if(HelperFile.isVideo(newPath)) {
						if(!MainFrame.playClicked){
							MainFrame.setToPlay();
						}	
						video.playMedia(newPath);
						HelperFile.setCurrentVideoPath(newPath);
						mainFrame.setTitle("VIDIVOX - "+ newPath);
						while(vidLength[0] == 0) {
							vidLength[0] = (int)((video.getLength())/1000);
						}	
						bar.setMaximum(vidLength[0]);
					} else {
						JOptionPane.showMessageDialog(mainFrame, "The file you have chosen is not a video, please try again.");
					}
				}
				
			}
		});
		mnFile.add(mntmOpenNewVideo);
		
		// Lets user restore the window back to original size
		JMenu mnWindow = new JMenu("Window");
		this.add(mnWindow);
			
		JMenuItem restoreWindow = new JMenuItem("Restore Window");
		restoreWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setBounds(100, 50, 1025, 675);				
			}
		});
		mnWindow.add(restoreWindow);		

		// Let user open the VIDIVOX user manual
		JMenu help = new JMenu("Help");
		this.add(help);
		
		JMenuItem mnInstructions = new JMenuItem("VIDIVOX User Manual");
		mnInstructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (Desktop.isDesktopSupported()) {
		                File userManual = new File( System.getProperty("user.dir")+"/VIDIVOX_USER_MANUAL");
		                try {
							Desktop.getDesktop().open(userManual);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
			}
		});
		help.add(mnInstructions);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}
}
