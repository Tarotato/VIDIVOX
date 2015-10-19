	package vidivox_beta;
	import java.awt.BorderLayout;
	import java.awt.Dimension;
	import java.awt.FlowLayout;
	import java.awt.Font;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.awt.event.KeyAdapter;
	import java.awt.event.KeyEvent;
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
	import javax.swing.JSlider;
	import javax.swing.JTextField;
	import javax.swing.filechooser.FileNameExtensionFilter;
	
	import uk.co.caprica.vlcj.player.MediaPlayer;
	
	/**
	 * @author Isabel Zhuang
	 * Class holds all the implementation for right side of the split pane from MainFrame
	 */
	@SuppressWarnings("serial")
	public class TextEditingPanel extends JPanel{
		
		private ArrayList<Integer> killPID = new ArrayList<Integer>();
		int festID = 0; // Process ID is very unlikely to be 0
		protected static PleaseWaitDialog waitDialog;
		
		public TextEditingPanel(final MediaPlayer video, final String[] videoName, final JFrame thisFrame, final JTextField insertionTime){
			
			// Create the panels to put in the right side of the split pane
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
			
			JPanel southPanel = new JPanel(); // Another panel for formatting purposes
			this.add(southPanel, BorderLayout.SOUTH);
					
			// Text are implementation--------------------------------------------------------------------------->
			
			JScrollPane scrollPane = new JScrollPane();
			centerPanel.add(scrollPane);
			
			final CommentaryTextArea txtrCommentary = new CommentaryTextArea(); // TextArea for user to enter their commentary
			txtrCommentary.setLineWrap(true);
			scrollPane.setViewportView(txtrCommentary);
	
			// Festival speak and stop implementation--------------------------------------------------------------------------->
			
			centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			
			JPanel festivalControl = new JPanel(); // Another panel for formatting purposes
			centerPanel.add(festivalControl);
			festivalControl.setLayout(new BoxLayout(festivalControl, BoxLayout.X_AXIS));
			
			final JButton btnSpeak = new JButton("Speak");
			btnSpeak.setEnabled(false);
			
			festivalControl.add(btnSpeak);
			
			final JButton btnStop = new JButton("Stop");
			btnStop.setEnabled(false);
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
					
			btnSpeak.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String pattern= "^[a-zA-Z0-9., ]*$"; // Takes in only numbers, letters, comma, full stop and spaces
			        if(txtrCommentary.getText().matches(pattern)&&!txtrCommentary.getText().equals("")){			
						if(killPID.isEmpty()){
							btnStop.setEnabled(true);
							System.out.println(killPID.size());
						// Speak commentary to the user through festival text-to-speech (DOESNT WORK PROPERLY I KNOW)
						BgFestival bg = new BgFestival(txtrCommentary.getText(), killPID);
						bg.execute();
						}
					killPID.removeAll(killPID);	
			        }else{
			        	JOptionPane.showMessageDialog(thisFrame, "Enter numbers and letters only. Please try again.");
			        }
				}
			});
			
			// Festival editing implementation [UNDER CONSTRUCTION]--------------------------------------------------------------------------->
			JPanel audioOptions = new JPanel();
			audioOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			centerPanel.add(audioOptions);		
	
			JLabel lblPitch = new JLabel("Pitch (Hz)");
			lblPitch.setFont(new Font("Tahoma", Font.PLAIN, 12));
			audioOptions.add(lblPitch);
			
			JSlider pitchSlider = new JSlider(-2, 2, 0); 
			pitchSlider.setMinorTickSpacing(1);
			pitchSlider.setMajorTickSpacing(1);
			pitchSlider.setPaintTicks(true);
		    pitchSlider.setPaintLabels(true);
			audioOptions.add(pitchSlider);
						
			JLabel lblEcho = new JLabel("Echo");
			lblEcho.setFont(new Font("Tahoma", Font.PLAIN, 12));
			audioOptions.add(lblEcho);
			
			JSlider echoSlider = new JSlider(-2, 2, 0); 
			echoSlider.setMinorTickSpacing(1);
			echoSlider.setMajorTickSpacing(1);
			echoSlider.setPaintTicks(true);
			echoSlider.setPaintLabels(true);
			audioOptions.add(echoSlider);
			
			audioOptions.add(Box.createRigidArea(new Dimension(0, 100))); // Blank space for formatting purposes
					
			JLabel lblinsertionTime = new JLabel("Insert at Time:");
			lblinsertionTime.setFont(new Font("Tahoma", Font.PLAIN, 12));
			audioOptions.add(lblinsertionTime);
			
			audioOptions.add(insertionTime);
			
			JButton btndefaultTime = new JButton("Set Current Time");
			btndefaultTime.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int currentTime = (int)(video.getTime()/1000); //Get current time in seconds				
					ProgressDisplayPanel.silenceTime = currentTime;	//Set length of silent buffer needed for mp3 conversion		
					
					// Displays time in hrs, mins and secs
					if(currentTime/60 < 1){
						insertionTime.setText(Integer.toString(currentTime)+ " s"); // Update the Time textfield
					}
					else if(currentTime/60 >= 1){
						insertionTime.setText( Integer.toString((currentTime)/60)+"m "+Integer.toString((currentTime)%60)+ "s"); // Update the Time textfield
					}
					else if(currentTime/3600 >= 1){
						insertionTime.setText( Integer.toString((currentTime)/3600)+"h "+Integer.toString((currentTime)%3600/60)+"m "+Integer.toString((currentTime)%3600%60)+ "s"); // Update the Time textfield
					}	
					
					
				}
			});
			
			audioOptions.add(btndefaultTime);
			
			// MP3 saving and merging implementation--------------------------------------------------------------------------->		
			
			// Let user select an .mp3 file to merge with current video
			JPanel commentaryOptions = new JPanel(); // Another panel for formatting purposes
			southPanel.add(commentaryOptions);		
	
			// Save input in text area as .wav file and convert it to an .mp3
			final JButton btnSaveAs = new JButton("Save as MP3");
			btnSaveAs.setEnabled(false);
			btnSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					String pattern= "^[a-zA-Z0-9., ]*$"; // Takes in only numbers, letters, comma, full stop and spaces
			        if(txtrCommentary.getText().matches(pattern)){					
						// Check that the number of words in the commentary is greater than 0 but no more than 40
			   			StringTokenizer st = new StringTokenizer(txtrCommentary.getText());
			   			st.countTokens();
			   			
			   			if (st.countTokens() > 0 && st.countTokens() <= 40) {
			   				// Prompt user for what they want to name their mp3 file
			   				JDialog saveDialog = new saveAsDialog("mp3", txtrCommentary.getText());
			   				saveDialog.setModal(true);
			   				saveDialog.setVisible(true);
			   			} else {
			   				JOptionPane.showMessageDialog(thisFrame, "Enter between 1 and 40 words. Please try again.");
			   			}
			        }else{
			        	JOptionPane.showMessageDialog(thisFrame, "Enter numbers and letters only. Please try again.");
			        }
				}
			});
			commentaryOptions.add(btnSaveAs);
			
			//--------------------------------------------------------------------------->
			
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
	
				    	if(HelperFile.isMp3(mp3Path)){			    		
				    		// Prompt user for a merged video name
							saveAsDialog saveVideo = new saveAsDialog("video", null);
							saveVideo.setModal(true);
							saveVideo.setVisible(true);
							
							try {							
								// Find out if any .mp3 file in MP3Files folder has the same name user has entered for MP3 file name
								String cmd = "find | grep -x \"./VideoFiles/" + videoName[0] +".avi\" | wc -l";
								ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
								Process process = builder.start();
								
								builder.redirectErrorStream(true);
								InputStream stdout = process.getInputStream();
								BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
										
								String line = stdoutBuffered.readLine();								
								
								// Generate an .avi file if none already exists
								if(!saveVideo.cancelClicked){
									if(line.equals("0")) {
										String videoPath = HelperFile.getCurrentVideoPath(); // Video to merge with is the one currently playing
									
										//HelperFile.mergeMp3(mp3Path, videoPath, thisFrame, videoName);
										
										JOptionPane.showMessageDialog(thisFrame, "Your video will start merging as soon as you click OK, please be patient while it merges.", null , JOptionPane.PLAIN_MESSAGE);
										
										waitDialog = new PleaseWaitDialog();
										waitDialog.setVisible(true);
										// Merge video as a background task
										BgMergeFiles mergeFiles = new BgMergeFiles(videoPath, mp3Path, waitDialog, thisFrame, video);
										mergeFiles.execute();
										
									} else if(!videoName[0].equals("")){
										// Error dialog if name of mp3 already exists
										JOptionPane.showMessageDialog(thisFrame, "This name is taken. Please choose another.", null , JOptionPane.INFORMATION_MESSAGE);
									}
								}
							
							} catch (IOException e1) {
								e1.printStackTrace();
							}
											    		
				    	} else {
				    		// Navigate to an error dialog
				    		JOptionPane.showMessageDialog(thisFrame, "Please make sure the file you have chosen is an audio file (.mp3).", null , JOptionPane.INFORMATION_MESSAGE);
				    	}
				    }
				}
			});
			commentaryOptions.add(btnMerge);	
			
			// Key listener for commentary enables and disables buttons for text area
			txtrCommentary.addKeyListener(new KeyAdapter() {
			    public void keyReleased(KeyEvent event) {
			    	 
			        String content = txtrCommentary.getText();
			        if (!content.equals("")) {
			        	btnSpeak.setEnabled(true);
			        	btnSaveAs.setEnabled(true);
			        } else {
			        	btnSpeak.setEnabled(false);
			        	btnStop.setEnabled(false);
			        	btnSaveAs.setEnabled(false);
			        }
			    }
			});
		}
	
	}
