package VIDIVOX_prototype;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
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

import java.util.ArrayList;
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
	//static boolean muteClicked = false;
	int[] muteClicked = {1};
	static boolean stopForward = false;
	private ArrayList<Integer> killPID = new ArrayList<Integer>();
	
	protected EmbeddedMediaPlayerComponent component = new EmbeddedMediaPlayerComponent();
	protected MediaPlayer video;
	protected static String currentVideoPath;
	protected static String mp3Name = null;
	protected static String videoName = null;
	
	/**
	 * Create the frame.
	 */
	public MainFrame(String videoPath) {
		setTitle("VIDIVOX prototype - Video editing Platform");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 1000, 650);
		final JFrame thisFrame = this;
		currentVideoPath = videoPath;
		
		// Top menu bar implementation -------------------------------------------------->
		CustomMenuBar menuBar = new CustomMenuBar(video, this);
		setJMenuBar(menuBar);
	
		
		// Video player implementation -------------------------------------------------->
		//VideoPanel videoPanel = new VideoPanel(video, component);
		JPanel videoPanel = new JPanel(); // Left side of the split pane
        videoPanel.setLayout(new BorderLayout());
        
        // Add a media component
        //component = new EmbeddedMediaPlayerComponent();
        videoPanel.add(component, BorderLayout.CENTER);
        video = component.getMediaPlayer();
        
        //-------------------------------------------------->
	
		JPanel progress = new JPanel(); // Holds the time in seconds and the progress bar (in controls Panel)
		
		final JLabel lblTime = new JLabel("0 s"); // Shows the time in seconds since the start of video (GUI)
		
		final JProgressBar bar = new JProgressBar(); // Shows the progress of the video (GUI)

		JPanel videoControlPanel = new JPanel(); // Holds all the video control buttons (in controls Panel)
		
		CustomControlPanel controls = new CustomControlPanel(videoPanel, progress, lblTime, bar, videoControlPanel);
		
		//-------------------------------------------------->
		
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
		videoControlPanel.add(btnSkipBack);
		
		btnRewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Continues rewinding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon("buttons/play.png")); // Set button to play
				BgForward rewind = new BgForward(-500, video); // Make a new background task
				rewind.execute();
			}
		});
		videoControlPanel.add(btnRewind);
		
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
		videoControlPanel.add(btnPlay);

		btnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Continues forwarding until user clicks play
				playClicked = false;
				btnPlay.setIcon(new ImageIcon("buttons/play.png")); // Set button to play
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
		
		//-------------------------------------------------->
		
		CustomVolumeControlPanel volume_control = new CustomVolumeControlPanel(video, muteClicked);
		controls.add(volume_control);	
		
		videoPanel.setMinimumSize(new Dimension(300, 500)); // Sets minimum dimensions for resizing purposes
		
		// Audio editing implementation ---------------------------------------------------->
		JPanel rightContentPanel = new JPanel();
		rightContentPanel.setLayout(new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS));
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		rightContentPanel.add(tabbedPane);
				
		TextEditingPanel textEditingPanel = new TextEditingPanel(video, videoName, thisFrame); // Right side of the split pane
		tabbedPane.addTab("Commentary", null, textEditingPanel, null);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Audio Options", null, panel, null);
		
		// Adding the two different panels to the two sides of the split pane ---------------->
		JSplitPane splitPane = new JSplitPane();
		setContentPane(splitPane);
		splitPane.setResizeWeight(0.8); // Resizes the frames in a 8:2 ratio
		splitPane.setLeftComponent(videoPanel);
		splitPane.setRightComponent(rightContentPanel);
		splitPane.setDividerLocation(700 + splitPane.getInsets().left);		
		
		// Video manipulation implementation ------------------------------------------------->
		this.setVisible(true); // Set the frame to visible before playing the video
		
		video.playMedia(currentVideoPath); // Play the video
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
					// If video gets to the end, stop the fast forwarding
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
            	// If the video is muted, unmute before exiting the program
            	if(muteClicked[0] == 0) {
		    		video.mute();
		    	}
            }
        });
	}
}
