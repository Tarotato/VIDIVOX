package commentary_manipulation;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import vidivox_beta.HelperFile;
import vidivox_beta.MainFrame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/** 
 * @author Isabel Zhuang
 * Class contains implementation for generating a dialog for naming an mp3 or video file.
 */
@SuppressWarnings("serial")
public class SaveAsDialog extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField textField;
    protected boolean cancelClicked;
	
    /**
     * Create the dialog.
     */
    public SaveAsDialog(final String type, final String commentary) {    	
    	final JDialog thisDialog = this;
    	
    	// Formatting
        setBounds(200, 200, 425, 225);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNameYour = new JLabel("Name your MP3 file");
        if(type.equals("video")) {
        	lblNameYour = new JLabel("Name your merged video file");
        }
        lblNameYour.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNameYour.setHorizontalAlignment(SwingConstants.CENTER);
        lblNameYour.setBounds(75, 44, 350, 40);
        contentPanel.add(lblNameYour, BorderLayout.CENTER);
        
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.SOUTH);
        
        JLabel lblMpName = new JLabel("Name:");
        lblMpName.setBounds(90, 130, 110, 20);
        panel.add(lblMpName);
        
        textField = new JTextField();
        textField.setBounds(160, 130, 150, 20);
        panel.add(textField);
        textField.setColumns(10); 
        
        // Sets extension text depending on what the user has to chosen to save
        if(type.equals("mp3")) {
			JLabel lblmp3 = new JLabel(".mp3");
	        lblmp3.setBounds(350, 130, 110, 20);
	        panel.add(lblmp3);
		} else {
			JLabel lblmp3 = new JLabel(".avi");
	        lblmp3.setBounds(350, 130, 110, 20);
	        panel.add(lblmp3);
		}
                
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		// Compares if dialog is used for naming an mp3 file or video
        		if(!textField.getText().trim().equals("")){
	        		if(type.equals("mp3")) {
	        			HelperFile.saveAsMp3(commentary, textField.getText().trim(), thisDialog); // Will save commentary as mp3
	        		} else {
	        			MainFrame.videoName[0] = textField.getText().trim(); // Else gets the user-entered video name for executing further commands      			
	        			thisDialog.dispose();
	        		}	
        		}else{
        			JOptionPane.showMessageDialog(thisDialog, "Please enter a file name.", "Error: Invalid Input" , JOptionPane.ERROR_MESSAGE);
        		}
        	}
		});
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		cancelClicked = true;
        		thisDialog.dispose();
        	}
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);    
    }
}

