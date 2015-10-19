package vidivox_beta;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


/**
 * @author Isabel Zhuang
 * Class creates a JDialog with an indeterminate progress bar 
 * to show user when waiting for a video to finish merging
 */
@SuppressWarnings("serial")
public class PleaseWaitDialog extends JDialog{
	
	private final JPanel contentPanel = new JPanel();	
	JProgressBar progressBar;
	
	/**
     * Create the dialog.
     */
    public PleaseWaitDialog() {
    	// Formatting
        setBounds(200, 200, 400, 200);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        
        // Add JLabel
        JLabel lblNameYour = new JLabel("Please wait while the video is being merged");
        lblNameYour.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNameYour.setHorizontalAlignment(SwingConstants.CENTER);
        lblNameYour.setBounds(75, 44, 350, 40);
        contentPanel.add(lblNameYour, BorderLayout.CENTER);
        
        // Add an indeterminate progress bar
        progressBar = new JProgressBar();
        contentPanel.add(progressBar, BorderLayout.SOUTH);
        progressBar.setIndeterminate(true);
                
    }
}
