package vidivox_beta;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import javax.swing.JTextField;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

/**
 * @author Isabel Zhuang
 * Class contains implementation and graphical user interface code for the starting frame.
 */
@SuppressWarnings("serial")
public class StartFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtVideoPath;
	private String videoPath;
	boolean isVideo = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartFrame frame = new StartFrame();
					System.out.print("2nd one");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartFrame() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 150, 500, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		final JFrame thisFrame = this;
		
		JLabel lblChooseYourVideo = new JLabel("Choose Your Video");
		lblChooseYourVideo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblChooseYourVideo.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblChooseYourVideo, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		
		txtVideoPath = new JTextField();
		txtVideoPath.setText("/HOME/");
		panel_1.add(txtVideoPath);
		txtVideoPath.setColumns(30);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				// Allows user to choose their own video file to play
				JFileChooser videoChooser = new JFileChooser(System.getProperty("user.dir") + "/VideoFiles/");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Video File", "avi");
				videoChooser.setFileFilter(filter);
				int okReturnVal = videoChooser.showOpenDialog(getParent());
				if(okReturnVal == JFileChooser.APPROVE_OPTION) {
					videoPath = videoChooser.getSelectedFile().getPath();
				  	txtVideoPath.setText(videoPath);
				}
			}
		});
		panel_1.add(btnBrowse);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		
		final JCheckBox chckbxDefaultVid = new JCheckBox("Use Default Video"); // Tick if user wants to use the big buck bunny video
		panel_2.add(chckbxDefaultVid);
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_3.add(panel_4, BorderLayout.EAST);
		
		JButton btnNewButton = new JButton("Ok");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(chckbxDefaultVid.isSelected()) { // If the user chooses to use the bunny video
					thisFrame.dispose();
					new MainFrame("./VideoFiles/bunny.avi");
				} else if(HelperFile.isVideo(videoPath)) { // If the user has chosen a video
					thisFrame.dispose();
					new MainFrame(videoPath);	
				} else { // If the user has not chosen a video
					// Display an error dialog
					JOptionPane.showMessageDialog(thisFrame, "The file you have chosen is not a video, please try again.");
				}
			}
		});
		panel_4.add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(thisFrame, "Are you qure you want to quit?", "Exit", JOptionPane.OK_CANCEL_OPTION);
				if (n==0){
					thisFrame.dispose();
				}
			}
		});
		panel_4.add(btnCancel);
	}

}
