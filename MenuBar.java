package vidivox_beta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class MenuBar extends JMenuBar{
	
	public MenuBar(final MediaPlayer video, final MainFrame mainFrame, final JSplitPane splitPane, final int[] vidLength, final JProgressBar bar){
				
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
					if(HelperFile.isVideo(newPath)) {
						video.playMedia(newPath);
						HelperFile.setCurrentVideoPath(newPath);
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
		
		JMenu window = new JMenu("Window");
		this.add(window);
		
		JMenuItem hideEditor = new JMenuItem("Hide Side Panel");
		hideEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		window.add(hideEditor);
		
		JMenuItem restoreWindow = new JMenuItem("Restore Window");
		restoreWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setBounds(100, 50, 1000, 650);
				splitPane.setDividerLocation(700 + splitPane.getInsets().left);
			}
		});
		window.add(restoreWindow);
		

		
		JMenu help = new JMenu("Help");
		this.add(help);
		
		JMenuItem instructions = new JMenuItem("VIDIVOX Guide");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFrame guide = new JFrame();
				guide.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				guide.setBounds(300, 150, 400, 300);
				JScrollPane scrollPane = new JScrollPane();
				guide.add(scrollPane);
				
				JTextArea txtrGuide = new JTextArea(); // TextArea for user to enter their commentary
				txtrGuide.setLineWrap(true);
				txtrGuide.setEditable(false);
				//txtrGuide.setText("stuff[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display[0x7fb1d0001268] main vout display error: Failed to resize display");
				
				File file = new File(System.getProperty("user.dir")+"/VIDIVOX_Guide");
				
				FileReader reader;
				try {
					reader = new FileReader( file );
					BufferedReader br = new BufferedReader(reader);
				txtrGuide.read( br, null );
				br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				
				scrollPane.setViewportView(txtrGuide);
				
				guide.setVisible(true);
				//JOptionPane.showMessageDialog(mainFrame, "inrto", "VIDIVOX GUIDE", JOptionPane.CLOSED_OPTION);
				
			}
		});
		help.add(instructions);
	}

}
