package vidivox_beta;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JSplitPane;

import commentary_manipulation.TextEditingPanel;

import background_tasks.BgForward;

import uk.co.caprica.vlcj.player.MediaPlayer; //getTime(), skip(), mute(), pause(), play()
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent; 
import video_manipulation.MenuBar;
import video_manipulation.ProgressDisplayPanel;
import video_manipulation.VolumeControlPanel;

/**
 * @author Isabel Zhuang
 * Class contains implementation and graphical user interface code for the main frame.
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {	
	public static boolean playClicked = true;
	int[] muteClicked = {1}; // Initialize as array so final value can be changed
	public static boolean stopForward = false;	
	protected EmbeddedMediaPlayerComponent component = new EmbeddedMediaPlayerComponent();
	protected MediaPlayer video;
	protected static String currentVideoPath;
	protected static String mp3Name = null;
	public static String[] videoName = {""};	
	final protected int[] vidLength = {0}; // Initialize as array so final value can be changed
	final static JButton btnPlay = new JButton();;
	
	/**
	 * Create the frame.
	 */
	public MainFrame(String videoPath) {
		//setTitle("VIDIVOX - beta");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 1050, 700);
		final JFrame thisFrame = this;
		currentVideoPath = videoPath;

		// Video player implementation -------------------------------------------------->
		JPanel videoPanel = new JPanel(); // Left side of the split pane
        videoPanel.setLayout(new BorderLayout());
        videoPanel.setMinimumSize(new Dimension(300, 500)); // Sets minimum dimensions for resizing purposes
        
        // Add a media component
        videoPanel.add(component, BorderLayout.CENTER);
        video = component.getMediaPlayer();
       
        // Video controls implementation-------------------------------------------------->	
		JPanel progress = new JPanel(); // Holds the time in seconds and the progress bar (in controls Panel)		
		final JLabel lblTime = new JLabel("0 s"); // Shows the time in since the start of video (GUI)		
		final JProgressBar bar = new JProgressBar(); // Shows the progress of the video (GUI)
		JPanel videoControlPanel = new JPanel(); // Holds all the video control buttons (in controls Panel)
		final JLabel totalTime = new JLabel("0 s"); // Shows the total time of the video
		
		JTextField insertionTime = new JTextField(); // Holds the time of insertion the user selects
		insertionTime.setEditable(false);
		insertionTime.setColumns(8);
		
		ProgressDisplayPanel controls = new ProgressDisplayPanel(videoPanel, progress, lblTime, bar, videoControlPanel, video, insertionTime);
		
		// Button implementation-------------------------------------------------->		
		// Initialize all the buttons in video_control Panel
		JButton btnSkipBack = new JButton();
		btnSkipBack.setIcon(new ImageIcon(this.getClass().getResource("/buttons/skipb.png")));		
		JButton btnRewind = new JButton();
		btnRewind.setIcon(new ImageIcon(this.getClass().getResource("/buttons/rewind.png")));
		btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/pause.png")));		
		JButton btnForward = new JButton();
		btnForward.setIcon(new ImageIcon(this.getClass().getResource("/buttons/forward.png")));		
		JButton btnSkipForward = new JButton();
		btnSkipForward.setIcon(new ImageIcon(this.getClass().getResource("/buttons/skipf.png")));		
		btnSkipBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Skips backward 5 seconds every time it is clicked
				video.skip(-5000);
			}
		});
		videoControlPanel.add(btnSkipBack);
		
		btnRewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Continues rewinding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/play.png")));; // Set button to play
				BgForward rewind = new BgForward(-500, video); // Make a new background task
				rewind.execute();
			}
		});
		videoControlPanel.add(btnRewind);
		
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Play or pause video depending on boolean variable playClicked
				if(!playClicked) {
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/pause.png")));
					video.play(); // Play the video
					playClicked = true;
					stopForward = false;
				} else {
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/play.png")));
					video.pause(); // Pause the video
					playClicked = false;
				}
			}
		});
		videoControlPanel.add(btnPlay);

		btnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Continues forwarding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/play.png"))); // Set button to play
				BgForward forward = new BgForward(500, video); // Make a new background task
				forward.execute();
			}
		});
		videoControlPanel.add(btnForward);
		
		btnSkipForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Skips forward 5 seconds every time it is clicked
				video.skip(5000);
			}
		});
		videoControlPanel.add(btnSkipForward);
		
		// Volume control implementation-------------------------------------------------->		
		VolumeControlPanel volumeControl = new VolumeControlPanel(video, muteClicked);
		controls.add(volumeControl);		
		
		// Audio editing implementation ---------------------------------------------------->
		JPanel rightContentPanel = new JPanel();
		rightContentPanel.setLayout(new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS));
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		rightContentPanel.add(tabbedPane);
				
		TextEditingPanel textEditingPanel = new TextEditingPanel(video, videoName, thisFrame, insertionTime); // Right side of the split pane
		tabbedPane.addTab("Commentary", null, textEditingPanel, null);
		
		// Adding the two different panels to the two sides of the split pane ---------------->
		JSplitPane splitPane = new JSplitPane();
		setContentPane(splitPane);
		splitPane.setResizeWeight(0.8); // Resizes the frames in a 8:2 ratio
		splitPane.setLeftComponent(videoPanel);
		splitPane.setRightComponent(rightContentPanel);
		splitPane.setDividerLocation(700 + splitPane.getInsets().left);
		
		// Top menu bar implementation -------------------------------------------------->
		MenuBar menuBar = new MenuBar(video, this, splitPane, vidLength, bar, videoPanel);
		setJMenuBar(menuBar);
	
		// Video manipulation implementation ------------------------------------------------->
		this.setVisible(true); // Set the frame to visible before playing the video
		
		video.playMedia(currentVideoPath); // Play the video
		video.setVolume(50); // Set initial volume to 50 (same as JSlider default value)
		
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		    @Override
		    public void finished(MediaPlayer mediaPlayer) {
		        // Play button for playing again when video finishes playing
		    	playClicked = false;
		    	btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/buttons/play.png")));
		    	stopForward = true; // For stopping the BgForward SwingWorker implementation (fast forwarding)
		    }
		});
		
		// Set the length of the progress bar
		while(vidLength[0] == 0) {
			vidLength[0] = (int)((video.getLength())/1000);

		}	
		bar.setMaximum(vidLength[0]);
		
		// Timer for updating the label and progress bar every half a second
		Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((video.getTime())/1000)/60 < 1){
				lblTime.setText((video.getTime())/1000+ " s"); // Update the label
				bar.setValue((int)(video.getTime())/1000); // Update the progress bar
				}
				else if(((video.getTime())/1000)/60 >= 1){
				lblTime.setText( ((video.getTime())/1000)/60+"m"+((video.getTime())/1000)%60+ "s"); // Update the label
				bar.setValue((int)(video.getTime())/1000); // Update the progress bar
				}
				else if(((video.getTime())/1000)/3600 >= 1){
					lblTime.setText( ((video.getTime())/1000)/3600+"h"+((video.getTime())/1000)%3600/60+"m"+((video.getTime())/1000)%3600%60+ "s"); // Update the label
					bar.setValue((int)(video.getTime())/1000); // Update the progress bar
				}				
				if(video.getLength() == 0) {
					// If video gets to the end, stop the fast forwarding
					stopForward = true;
				}
			}
		});
		timer.start();

		// Add panel at bottom of class so total time can be set
		ProgressDisplayPanel.setTotalTime(video, totalTime);
		progress.add(totalTime);
		
		// For fixing problem where video being muted to start with, when last execution exits while muted.
	    addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	e.getWindow().dispose();
            	// If the video is muted, unmute before exiting the program
            	if(muteClicked[0] == 0) {
		    		video.mute();
		    	}
            }
        });
	    
	    setTitle("VIDIVOX - "+ System.getProperty("user.dir")+currentVideoPath);
	}
	// Method to display correct image of play button
	public static void setToPlay(){		
		btnPlay.doClick();
	}	
}

