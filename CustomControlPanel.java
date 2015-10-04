package VIDIVOX_prototype;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CustomControlPanel extends JPanel {
	
	public CustomControlPanel(JPanel videoPanel, JPanel progress, JLabel lblTime, JProgressBar bar, JPanel videoControlPanel){
		
		videoPanel.add(this, BorderLayout.SOUTH);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));				
		this.add(progress);
		
		progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));
				
		progress.add(lblTime);
				
		progress.add(bar);
		
		this.add(videoControlPanel);
		videoControlPanel.setLayout(new BoxLayout(videoControlPanel, BoxLayout.X_AXIS));
	}

}
