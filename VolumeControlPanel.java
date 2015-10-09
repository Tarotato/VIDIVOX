package vidivox_beta;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class VolumeControlPanel extends JPanel {

	public VolumeControlPanel(final MediaPlayer video, final int[] muteClicked) {
		this.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel(); // Panel used for layout purposes
		this.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblVolume = new JLabel("Volume"); // Label to tell user JSlider is for volume control													
		panel_1.add(lblVolume);
		lblVolume.setFont(new Font("Tahoma", Font.PLAIN, 15));

		final JButton btnMute = new JButton(); // Initialize btnMute here for use in JSlider actionListener												

		JSlider slider = new JSlider(); // JSlider for volume control
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// Change the volume of the video to the value obtained from the slider				
				video.setVolume(((JSlider) e.getSource()).getValue());
			}
		});
		panel_1.add(slider);

		btnMute.setIcon(new ImageIcon(this.getClass().getResource("/buttons/mute.png")));
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Toggle the mute for the video depending on boolean variable
				// muteClicked
				if (0 != muteClicked[0]) {
					btnMute.setIcon(new ImageIcon(this.getClass().getResource("/buttons/unmute.png")));
					video.mute(); // Toggles mute
					muteClicked[0] = 0;
				} else {
					btnMute.setIcon(new ImageIcon(this.getClass().getResource("/buttons/mute.png")));
					video.mute(); // Toggles mute
					muteClicked[0] = 1;
				}
			}
		});
		panel_1.add(btnMute);
	}

}
