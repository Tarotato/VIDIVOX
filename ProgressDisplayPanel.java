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
	
	public ProgressDisplayPanel(JPanel videoPanel, JPanel progress, JLabel lblTime, final JProgressBar bar, JPanel videoControlPanel, final MediaPlayer video, final JTextField insertionTime){
		
		videoPanel.add(this, BorderLayout.SOUTH);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));				
		this.add(progress);
		
		progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));
				
		progress.add(lblTime);
				
		bar.addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent e){

				int point = e.getX();
				int width = bar.getWidth();
				float fraction = (float)point/width;
				int time = (int) (fraction*(bar.getMaximum()));
				System.out.println(time);
				
				if(((video.getTime())/1000)/60 < 1){
					insertionTime.setText(Integer.toString(time)+ " s"); // Update the Time text box
				}
				else if(((video.getTime())/1000)/60 >= 1){
					insertionTime.setText( Integer.toString((time)/60)+"m "+Integer.toString((time)%60)+ "s"); // Update the Time text box 
				}
				else if(((video.getTime())/1000)/3600 >= 1){
					insertionTime.setText( Integer.toString((time)/3600)+"h "+Integer.toString((time)%3600/60)+"m "+Integer.toString((time)%3600%60)+ "s"); // Update the Time text box
				}				
			}
		});		
		
		progress.add(bar);
		
		this.add(videoControlPanel);
		
		
	}

}
