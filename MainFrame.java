import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

import java.awt.Component;
import java.io.IOException;

import javax.swing.SwingConstants;

import uk.co.caprica.vlcj.player.MediaPlayer; //getTime(), skip(), mute(), pause(), play()
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent; 

public class MainFrame extends JFrame {
	
	int festID = 0; //because process ID is very unlikely to be 0
	
	private final EmbeddedMediaPlayerComponent component;
	private final MediaPlayer video;
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("VIDIVOX by twerking-hippo :)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 1000, 650);
		
		
		//Top menu bar implementation -------------------------------------------------->
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenNewVideo = new JMenuItem("Open New Video...");
		mntmOpenNewVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//changing the video that we are editing
			}
		});
		mnFile.add(mntmOpenNewVideo);
		
		
		//Video player implementation -------------------------------------------------->
		JPanel videoPane = new JPanel(); //left side of the split pane
        videoPane.setLayout(new BorderLayout());
        
        //add a media component
        component = new EmbeddedMediaPlayerComponent();
        videoPane.add(component, BorderLayout.CENTER);
        video = component.getMediaPlayer();
		
		JPanel controls = new JPanel();
		videoPane.add(controls, BorderLayout.SOUTH);
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		
		JPanel progress = new JPanel();
		controls.add(progress);
		progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));
		
		final JLabel lblTime = new JLabel("0 secs"); //time label for the GUI
		progress.add(lblTime);
		
		final JProgressBar bar = new JProgressBar(); //progress bar of the GUI
		progress.add(bar);

		JPanel video_control = new JPanel(); //panel for holding all the control buttons (play/pause, rewind, forward)
		controls.add(video_control);
		video_control.setLayout(new BoxLayout(video_control, BoxLayout.X_AXIS));
		
		JButton btnReverse = new JButton("l <<");
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//should continue rewinding until user clicks play
			}
		});
		video_control.add(btnReverse);
		
		final JButton btnPlay = new JButton("ll");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//play or pause video
				if(btnPlay.getText().equals(">")) {
					btnPlay.setText("ll");
					video.play(); //play the video
				} else {
					btnPlay.setText(">");
					video.pause(); //pause the video
				}
			}
		});
		video_control.add(btnPlay);
		
		JButton btnForward = new JButton(">> l");
		btnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//should continue forwarding until user clicks play
			}
		});
		video_control.add(btnForward);
		
		JPanel volume_control = new JPanel(); //panel for holding the volume control buttons (jslider and mute btn)
		controls.add(volume_control);
		volume_control.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel(); //panel used for layout purposes
		volume_control.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblSound = new JLabel("Sound");
		panel_1.add(lblSound);
		lblSound.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JSlider slider = new JSlider();
		panel_1.add(slider);
		
		final JButton btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//mute the sound when clicked, unmute when clicked again
				if(btnMute.getText().equals("Mute")) {
					btnMute.setText("UnMute");
					video.mute(); //toggles mute for the video
				} else {
					btnMute.setText("Mute");
					video.mute(); //toggles mute for the video
				}
			}
		});
		panel_1.add(btnMute);
		videoPane.setMinimumSize(new Dimension(300, 500));
		
		//Audio editing implementation ---------------------------------------------------->
		
		JPanel audio_editing = new JPanel(); //the right side of the split pane
		audio_editing.setLayout(new BoxLayout(audio_editing, BoxLayout.Y_AXIS));
		audio_editing.setMinimumSize(new Dimension(300, 500));
	
		JPanel panel_2 = new JPanel();
		audio_editing.add(panel_2);
		
		JLabel lblEnterYourCommentary = new JLabel("Commentary here:");
		lblEnterYourCommentary.setHorizontalAlignment(SwingConstants.LEFT);
		lblEnterYourCommentary.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblEnterYourCommentary.setFont(new Font("Tahoma", Font.PLAIN, 15));
		audio_editing.add(lblEnterYourCommentary);
		
		final JTextArea txtrCommentary = new JTextArea();
		txtrCommentary.setText("(max 40 words)");
		txtrCommentary.setLineWrap(true);
		txtrCommentary.setPreferredSize(new Dimension(270, 300));
		audio_editing.add(txtrCommentary);
		
		JPanel audio_options = new JPanel();
		audio_editing.add(audio_options);
		
		JButton btnSpeak = new JButton("Speak");
		btnSpeak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//use festival to speak out what the user has inputed in text area
					
				//execute background process of festival
				String input = txtrCommentary.getText();
				BgFestival bg = new BgFestival(input);
				bg.execute();
			}
		});
		audio_options.add(btnSpeak);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//kill the process --------------------------------------------------------not working yet
				if(festID != 0) {
					String cmd = "kill "+(festID+4);
					ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
					try {
						builder.start();
						festID = 0;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		audio_options.add(btnStop);
		
		JButton btnSaveAs = new JButton("Save as MP3");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//save input in textarea as .wav file and convert to .mp3 and save
				
				String words = txtrCommentary.getText();
	   			StringTokenizer st = new StringTokenizer(words);
	   			st.countTokens();
	   			
	   			if (st.countTokens() <= 40){				
				JDialog saveDialog = new saveAsDialog(txtrCommentary.getText());
				saveDialog.setVisible(true);
	   			}else{
	   				JOptionPane.showMessageDialog(thisFrame, "Numbers of words in commentary exceeds 40. Please try again.");
	   			}
			}
		});
		audio_options.add(btnSaveAs);
		
		JPanel panel = new JPanel();
		audio_editing.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnMerge = new JButton("Merge With MP3");
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//merge mp3 with current video 
				//prompt user to choose mp3 file to merge with
			}
		});
		panel.add(btnMerge);
		
		
		//Adding the two different panels to the two sided of the split pane ---------------->
		JSplitPane splitPane = new JSplitPane();
		setContentPane(splitPane);
		splitPane.setLeftComponent(videoPane);
		splitPane.setRightComponent(audio_editing);
		splitPane.setDividerLocation(700 + splitPane.getInsets().left);
		
		
		//video manipulation implementation ------------------------------------------------->
		this.setVisible(true); //set the frame to visible before playing the video
		
		video.playMedia("bunny.avi"); //play the video
		
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		    @Override
		    public void finished(MediaPlayer mediaPlayer) {
		        //button to play when media player finished playing...
		    	btnPlay.setText(">");
		    }
		});
		
		int length = 0;
		while(length == 0) {
			length = (int)((video.getLength())/1000);
		}
		
		bar.setMaximum(length);
		
		Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// update the label every second
				lblTime.setText((video.getTime())/1000+ " secs");
				bar.setValue((int)(video.getTime())/1000);
			}
		});
		timer.start();
		
		//to fix problem for video being muted when last video exits while muted.
	    addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	e.getWindow().dispose();
            	//if the video is muted, unmuted before exiting the program
            	if(btnMute.getText() == "UnMute") {
		    		video.mute();
		    	}
            }
        });
	}

}