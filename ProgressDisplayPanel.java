package vidivox_beta;


import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class ProgressDisplayPanel extends JPanel {
	
	protected static int silenceTime = 0;
	
	public ProgressDisplayPanel(JPanel videoPanel, JPanel progress, JLabel lblTime, final JProgressBar bar, JPanel videoControlPanel, final MediaPlayer video, final JTextField insertionTime){
		
		// Add this Panel with the progress bar related objects to the videoPanel
		videoPanel.add(this, BorderLayout.SOUTH);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));				
		this.add(progress);
		
		progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));				
		progress.add(lblTime);
				
		// Progress bar has a listener that will set the Time textfield in the side panel if clicked
		bar.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e){

				int point = e.getX();
				int width = bar.getWidth();
				float fraction = (float)point/width;
				int time = (int) (fraction*(bar.getMaximum())); // Calculates time in seconds
				silenceTime = time;
				System.out.println(time);
				
				// Displays time in hrs, mins and secs
				if(((video.getTime())/1000)/60 < 1){
					insertionTime.setText(Integer.toString(time)+ " s"); // Update the Time textfield
				}
				else if(((video.getTime())/1000)/60 >= 1){
					insertionTime.setText( Integer.toString((time)/60)+"m "+Integer.toString((time)%60)+ "s"); // Update the Time textfield
				}
				else if(((video.getTime())/1000)/3600 >= 1){
					insertionTime.setText( Integer.toString((time)/3600)+"h "+Integer.toString((time)%3600/60)+"m "+Integer.toString((time)%3600%60)+ "s"); // Update the Time textfield
				}				
			}
		});		
		
		progress.add(bar);		
		this.add(videoControlPanel);		
	}
}
