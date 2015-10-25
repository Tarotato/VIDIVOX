package background_tasks;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import uk.co.caprica.vlcj.player.MediaPlayer;
import vidivox_beta.MainFrame;

/**
 * @author Isabel Zhuang
 * Class executes fast-forwarding and rewinding in the background until the user clicks play as a background task
 */
public class BgForward extends SwingWorker<Void, Void> {	
	private int interval;
	private MediaPlayer video;
	public BgForward(int interval, MediaPlayer video) {
		this.interval = interval;
		this.video = video;
	}	
	/* 
	 * Uses javas.swing.Timer to continually skip bits of video to simulate fast forward and rewind
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		// Skip by interval every time Timer clicks, negative = rewind, positive = forward
		Timer timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!MainFrame.playClicked) { // While user has not clicked play
					if(!MainFrame.stopForward) { // And While it is not the start or end of the video
						video.skip(interval); // Keep skipping
					} else {
						((Timer)e.getSource()).stop(); 
					}
				} else {
					((Timer)e.getSource()).stop();
				}
			}
		});
		timer.start();		
		return null;
	}
}
