package VIDIVOX_prototype;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class TextEditingPanel extends JPanel{
	
	private ArrayList<Integer> killPID = new ArrayList<Integer>();
	int festID = 0; // Process ID is very unlikely to be 0
	
	public TextEditingPanel(final MediaPlayer video, final String videoName, final JFrame thisFrame){
		
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(300, 500));

		JPanel westPanel = new JPanel(); // Another panel for formatting purposes
		this.add(westPanel, BorderLayout.WEST);
		westPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		JPanel eastPanel = new JPanel(); // Another panel for formatting purposes
		this.add(eastPanel, BorderLayout.EAST);
		eastPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		JPanel centerPanel = new JPanel(); // Another panel for formatting purposes, holds all the panels in the center panel
		this.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));	
		
		JLabel lblEnterYourCommentary = new JLabel("Enter your commentary here:");
		lblEnterYourCommentary.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JPanel northPanel = new JPanel(); // Another panel for formatting purposes
		this.add(northPanel, BorderLayout.NORTH);
		northPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		northPanel.add(lblEnterYourCommentary, BorderLayout.NORTH);
				
		//--------------------------------------------------------------------------->
		
		JScrollPane scrollPane = new JScrollPane();
		centerPanel.add(scrollPane);
		
		final CommentaryTextArea txtrCommentary = new CommentaryTextArea(); // TextArea for user to enter their commentary
		txtrCommentary.setLineWrap(true);
		scrollPane.setViewportView(txtrCommentary);

		//--------------------------------------------------------------------------->
		
		centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JPanel festivalControl = new JPanel(); // Another panel for formatting purposes
		centerPanel.add(festivalControl);
		festivalControl.setLayout(new BoxLayout(festivalControl, BoxLayout.X_AXIS));
		
		JButton btnSpeak = new JButton("Speak");
		btnSpeak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(killPID.isEmpty()){
					
					System.out.println(killPID.size());
				// Speak commentary to the user through festival text-to-speech (DOESNT WORK PROPERLY)
				BgFestival bg = new BgFestival(txtrCommentary.getText(), killPID);
				bg.execute();
				}
				System.out.println(killPID.size());
				killPID.removeAll(killPID);			
				System.out.println(killPID.size());
			}
		});
		festivalControl.add(btnSpeak);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Kill the festival process (Stop speaking)
				if (!killPID.isEmpty()) {
					if (killPID.get(0) != 0) {
						festID = killPID.get(0)+4;
						String cmd = "kill " + (festID);
						ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
						try {
							builder.start();
							killPID.set(0, 0);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		festivalControl.add(btnStop);
		centerPanel.add(festivalControl);
		
		centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));  // Blank space for formatting purposes
		
		//--------------------------------------------------------------------------->
		
		JPanel commentaryOptions = new JPanel(); // Another panel for formatting purposes
		centerPanel.add(commentaryOptions);		
				
		JLabel lblsaveCommentary = new JLabel("Save your commentary as an MP3:", SwingConstants.CENTER);
		lblsaveCommentary.setFont(new Font("Tahoma", Font.PLAIN, 12));
		commentaryOptions.add(lblsaveCommentary);
		
		// Save input in text area as .wav file and convert it to an .mp3
		JButton btnSaveAs = new JButton("Save as MP3");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Check that the number of words in the commentary is greater than 0 but no more than 40
	   			StringTokenizer st = new StringTokenizer(txtrCommentary.getText());
	   			st.countTokens();
	   			
	   			if (st.countTokens() > 0 && st.countTokens() <= 40) {
	   				// Prompt user for what they want to name their mp3 file
	   				JDialog saveDialog = new saveAsDialog("mp3", txtrCommentary.getText());
	   				saveDialog.setVisible(true);
	   			} else {
	   				JOptionPane.showMessageDialog(thisFrame, "Enter between 1 and 40 words. Please try again.");
	   			}
			}
		});
		commentaryOptions.add(btnSaveAs, BorderLayout.CENTER);	
		
		commentaryOptions.add(Box.createRigidArea(new Dimension(0, 50))); // Blank space for formatting purposes
		
		//--------------------------------------------------------------------------->
		
		JLabel lblmergeWith = new JLabel("Merge your current video with an MP3:");
		lblmergeWith.setFont(new Font("Tahoma", Font.PLAIN, 12));
		commentaryOptions.add(lblmergeWith);
		
		commentaryOptions.add(Box.createRigidArea(new Dimension(0, 50))); // Blank space for formatting purposes
		
		// Let user select an .mp3 file to merge with current video
		JButton btnMerge = new JButton("Merge With MP3");
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				String mp3Path = null;
				
				// Let user select an mp3
				JFileChooser mp3Chooser = new JFileChooser(System.getProperty("user.dir") + "/MP3Files/");
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 File", "mp3");
			    mp3Chooser.setFileFilter(filter);
			    int okReturnVal = mp3Chooser.showOpenDialog(getParent());
			    if(okReturnVal == JFileChooser.APPROVE_OPTION) {
			    	mp3Path = mp3Chooser.getSelectedFile().getPath();

			    	if(VideoMethods.isMp3(mp3Path)){			    		
			    		// Prompt user for a merged video name
						saveAsDialog saveVideo = new saveAsDialog("video", null);
						saveVideo.setModal(true);
						saveVideo.setVisible(true);
						
						try {							
							// Find out if any .mp3 file in MP3Files folder has the same name user has entered for MP3 file name
							String cmd = "find | grep -x \"./VideoFiles/" + videoName +".avi\" | wc -l";
							ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
							Process process = builder.start();
							
							builder.redirectErrorStream(true);
							InputStream stdout = process.getInputStream();
							BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
									
							String line = stdoutBuffered.readLine();								
									
							// Generate an .avi file if none already exists
							if(!saveVideo.cancelClicked){
								if(line.equals("0")) {
									String videoPath = VideoMethods.getCurrentVideoPath(); // Video to merge with is the one currently playing
									VideoMethods.mergeMp3(mp3Path, videoPath);
									int n = JOptionPane.showConfirmDialog((Component) null, "Successfully merged "+ VideoMethods.getBasename(mp3Path) +" with "+ VideoMethods.getBasename(videoPath) +".\n Would you like to play it now?", "alert", JOptionPane.OK_CANCEL_OPTION);
					    		
					    			if(n == 0) { // Change the video to output.avi if user selects "OK"
					    				video.playMedia("VideoFiles/"+videoName+".avi");
					    				VideoMethods.setCurrentVideoPath(System.getProperty("user.dir") + "VideoFiles/"+videoName+".avi");
					    			}									
								} else {
									// Error dialog if name of mp3 already exists
									JOptionPane.showMessageDialog(thisFrame, "This name is taken. Please choose another.");
								}
							}									
						} catch (IOException e1) {
							e1.printStackTrace();
						}					    		
			    	} else {
			    		// Navigate to an error dialog
			    		JOptionPane.showMessageDialog(thisFrame, "Please make sure the file you have chosen is an audio file (.mp3).");
			    	}
			    }
			}
		});
		commentaryOptions.add(btnMerge);	
	}

}
