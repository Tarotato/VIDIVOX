import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;

import uk.co.caprica.vlcj.player.MediaPlayer; //getTime(), skip(), mute(), pause(), play()
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent; 

/**
 * @author Isabel Zhuang and Rebecca Lee
 * Class contains implementation and graphical user interface code for the main frame.
 */
public class MainFrame extends JFrame {
	
	int festID = 0; //because process ID is very unlikely to be 0
	static boolean playClicked = true;
	static boolean muteClicked = false;
	static boolean stopForward = false;
	private ArrayList<Integer> killPID = new ArrayList<Integer>();
	
	private final EmbeddedMediaPlayerComponent component;
	private final MediaPlayer video;
	
	/**
	 * Create the frame.
	 */
	public MainFrame(String videoPath) {
		setTitle("VIDIVOX by twerking-hippo :)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 1000, 650);
		final JFrame thisFrame = this;
		
		// Top menu bar implementation -------------------------------------------------->
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenNewVideo = new JMenuItem("Open New Video...");
		mntmOpenNewVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Changing the video that we are editing
				// Prompt user for the video they want to change to
				String newPath;
				JFileChooser videoChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Video File", "avi");
				videoChooser.setFileFilter(filter);
				int okReturnVal = videoChooser.showOpenDialog(getParent());
				if(okReturnVal == JFileChooser.APPROVE_OPTION) {
					newPath = videoChooser.getSelectedFile().getPath();
					// Check if file chosen is a video, if yes, change video, if not, show error dialog and do nothing
					if(File.isVideo(newPath)) {
						video.playMedia(newPath);
					} else {
						JOptionPane.showMessageDialog(thisFrame, "The file you have chosen is not a video, please try again.");
					}
				}
				
			}
		});
		mnFile.add(mntmOpenNewVideo);
		
		
		// Video player implementation -------------------------------------------------->
		JPanel videoPane = new JPanel(); // Left side of the split pane
        videoPane.setLayout(new BorderLayout());
        
        // Add a media component
        component = new EmbeddedMediaPlayerComponent();
        videoPane.add(component, BorderLayout.CENTER);
        video = component.getMediaPlayer();
		
		JPanel controls = new JPanel(); // Holds everything under the video player
		videoPane.add(controls, BorderLayout.SOUTH);
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		
		JPanel progress = new JPanel(); // Holds the time in seconds and the progress bar (in controls Panel)
		controls.add(progress);
		progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));
		
		final JLabel lblTime = new JLabel("0 s"); // Shows the time in seconds since the start of video (GUI)
		progress.add(lblTime);
		
		final JProgressBar bar = new JProgressBar(); // Shows the progress of the video (GUI)
		progress.add(bar);

		JPanel video_control = new JPanel(); // Holds all the video control buttons (in controls Panel)
		controls.add(video_control);
		video_control.setLayout(new BoxLayout(video_control, BoxLayout.X_AXIS));
		
		// Initialize all the buttons in video_control Panel
		JButton btnSkipBack = new JButton();
		btnSkipBack.setIcon(new ImageIcon("buttons/skipb.png"));
		JButton btnRewind = new JButton();
		btnRewind.setIcon(new ImageIcon("buttons/rewind.png"));
		final JButton btnPlay = new JButton();
		btnPlay.setIcon(new ImageIcon("buttons/pause.png"));
		JButton btnForward = new JButton();
		btnForward.setIcon(new ImageIcon("buttons/forward.png"));
		JButton btnSkipForward = new JButton();
		btnSkipForward.setIcon(new ImageIcon("buttons/skipf.png"));
		
		btnSkipBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Skips backward 5 seconds every time it is clicked
				video.skip(-5000);
			}
		});
		video_control.add(btnSkipBack);
		
		btnRewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Continues rewinding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon("buttons/play.png")); // Set button to play
				BgForward rewind = new BgForward(-500, video); // Make a new background task
				rewind.execute();
			}
		});
		video_control.add(btnRewind);
		
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Play or pause video depending on boolean variable playClicked
				if(!playClicked) {
					btnPlay.setIcon(new ImageIcon("buttons/pause.png"));
					video.play(); // Play the video
					playClicked = true;
					stopForward = false;
				} else {
					btnPlay.setIcon(new ImageIcon("buttons/play.png"));
					video.pause(); // Pause the video
					playClicked = false;
				}
			}
		});
		video_control.add(btnPlay);

		btnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Continues forwarding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon("buttons/play.png")); // Set button to play
				BgForward forward = new BgForward(500, video); // Make a new background task
				forward.execute();
			}
		});
		video_control.add(btnForward);
		
		btnSkipForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Skips forward 5 seconds every time it is clicked
				video.skip(5000);
			}
		});
		video_control.add(btnSkipForward);
		
		JPanel volume_control = new JPanel(); // Holds the volume control buttons (JSlider and Mute button)
		controls.add(volume_control);
		volume_control.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel(); // Panel used for layout purposes
		volume_control.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblVolume = new JLabel("Volume"); // Label to tell user JSlider is for volume control
		panel_1.add(lblVolume);
		lblVolume.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JSlider slider = new JSlider(); // JSlider for volume control
		slider.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	        	// Change the volume of the video to the value obtained from the slider
	            video.setVolume(((JSlider) e.getSource()).getValue());
	        }
	    });
		panel_1.add(slider);
		
		final JButton btnMute = new JButton();
		btnMute.setIcon(new ImageIcon("buttons/mute.png"));
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Toggle the mute for the video depending on boolean variable muteClicked
				if(!muteClicked) {
					btnMute.setIcon(new ImageIcon("buttons/unmute.png"));
					video.mute(); // Toggles mute
					muteClicked = true;
				} else {
					btnMute.setIcon(new ImageIcon("buttons/mute.png"));
					video.mute(); // Toggles mute
					muteClicked = false;
				}
			}
		});
		panel_1.add(btnMute);
		videoPane.setMinimumSize(new Dimension(300, 500)); // Sets minimum dimensions for resizing purposes
		
		
		// Audio editing implementation ---------------------------------------------------->
		JPanel audio_editing = new JPanel(); // Right side of the split pane
		audio_editing.setLayout(new BoxLayout(audio_editing, BoxLayout.Y_AXIS));
		audio_editing.setMinimumSize(new Dimension(300, 500));

		JLabel lblEnterYourCommentary = new JLabel("Commentary here:"); // Label to tell user what the text area is for
		lblEnterYourCommentary.setHorizontalAlignment(SwingConstants.LEFT);
		lblEnterYourCommentary.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblEnterYourCommentary.setFont(new Font("Tahoma", Font.PLAIN, 15));
		audio_editing.add(lblEnterYourCommentary);
		
		final JTextArea txtrCommentary = new JTextArea(); // TextArea for user to enter their commentary
		txtrCommentary.setText("(max 40 words)");
		txtrCommentary.setLineWrap(true);
		audio_editing.add(txtrCommentary);
		
		JPanel audio_options = new JPanel(); // Holds all buttons below JTextArea
		audio_editing.add(audio_options);
		
		JButton btnSpeak = new JButton("Speak");
		btnSpeak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Speak commentary to the user through festival text-to-speech
				String input = txtrCommentary.getText();
				BgFestival bg = new BgFestival(input, killPID);
				bg.execute();
				killPID.removeAll(killPID);
			}
		});
		audio_options.add(btnSpeak);
		
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
		audio_options.add(btnStop);
		
		// Save input in text area as .wav file and convert it to an .mp3
		JButton btnSaveAs = new JButton("Save as MP3");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Check that the number of words in the commentary is greater than 0 but no more than 40
				String words = txtrCommentary.getText();
	   			StringTokenizer st = new StringTokenizer(words);
	   			st.countTokens();
	   			
	   			if (st.countTokens() > 0 || st.countTokens() <= 40) {
	   				// Prompt user for what they want to name their mp3 file
	   				JDialog saveDialog = new saveAsDialog(txtrCommentary.getText());
	   				saveDialog.setVisible(true);
	   			} else {
	   				JOptionPane.showMessageDialog(thisFrame, "Enter between 1 and 40 words. Please try again.");
	   			}
			}
		});
		audio_options.add(btnSaveAs);
		
		JPanel panel = new JPanel(); // Another panel for formatting purposes
		audio_editing.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Let user select an .mp3 file to merge with current video
		JButton btnMerge = new JButton("Merge With MP3");
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String mp3Path = null;
				
				// Let user select an mp3
				JFileChooser mp3Chooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 File", "mp3");
			    mp3Chooser.setFileFilter(filter);
			    int okReturnVal = mp3Chooser.showOpenDialog(getParent());
			    if(okReturnVal == JFileChooser.APPROVE_OPTION) {
			    	mp3Path = mp3Chooser.getSelectedFile().getPath();

			    	if(File.isMp3(mp3Path)){
			    		
			    		String videoPath = "bunny.avi"; // ***SOMETHING with changing to either USER SELECTED or BUNNY AVI***
			    		File.mergeMp3(mp3Path, videoPath);
			    		int n = JOptionPane.showConfirmDialog((Component) null, "Successfully merged "+ File.getBasename(mp3Path) +" with "+ File.getBasename(videoPath) +".\n Would you like to play it now?", "alert", JOptionPane.OK_CANCEL_OPTION);
			    		
			    		if(n == 0) { // Change the video to output.avi if user selects "OK"
		    				video.playMedia("output.avi");
			    		}
			    		
			    	} else {
			    		// Navigate to an error dialog
			    		JOptionPane.showMessageDialog(thisFrame, "Please make sure the file you have chosen is an audio file (.mp3).");
			    	}
			    }
			}
		});
		panel.add(btnMerge);
		
		
		// Adding the two different panels to the two sides of the split pane ---------------->
		JSplitPane splitPane = new JSplitPane();
		setContentPane(splitPane);
		splitPane.setResizeWeight(0.8); // Resizes the frames in a 8:2 ratio
		splitPane.setLeftComponent(videoPane);
		splitPane.setRightComponent(audio_editing);
		splitPane.setDividerLocation(700 + splitPane.getInsets().left);
		
		
		// Video manipulation implementation ------------------------------------------------->
		this.setVisible(true); // Set the frame to visible before playing the video
		
		video.playMedia(videoPath); // Play the video
		video.setVolume(50); // Set initial volume to 50 (same as JSlider default value)
		
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		    @Override
		    public void finished(MediaPlayer mediaPlayer) {
		        // Play button for playing again when video finishes playing
		    	playClicked = false;
		    	btnPlay.setIcon(new ImageIcon("buttons/play.png"));
		    	stopForward = true; // For stopping the BgForward SwingWorker implementation (fast forwarding)
		    }
		});
		
		final int[] vidLength = {0}; // Initialize as array so final value can be changed
		while(vidLength[0] == 0) {
			vidLength[0] = (int)((video.getLength())/1000);
		}
		
		bar.setMaximum(vidLength[0]);
		
		// Timer for updating the label and progress bar every half a second
		Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblTime.setText((video.getTime())/1000+ " s"); // Update the label
				bar.setValue((int)(video.getTime())/1000); // Update the progress bar
				if(video.getLength() == 0) {
					// If video gets to the end, stop the rewinding
					stopForward = true;
				}
			}
		});
		timer.start();
		
		// For fixing problem where video being muted to start with, when last execution exits while muted.
	    addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	e.getWindow().dispose();
            	// If the video is muted, unmuted before exiting the program
            	if(muteClicked) {
		    		video.mute();
		    	}
            }
        });
	}
}

