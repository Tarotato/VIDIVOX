package commentary_manipulation;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

/**
 * @author Isabel Zhuang
 * Class sets the text area when the user first opens VIDIVOX
 * This occurs only once when VIDIVOX is first run
 */
@SuppressWarnings("serial")
public class CommentaryTextArea extends JTextArea implements FocusListener{	
	boolean i = true;	
	
	public CommentaryTextArea(){
		this.addFocusListener(this);
		// Placeholder for text area when VIDIVOX is first run
		this.setText("Enter your commentary here"+ System.getProperty("line.separator") +"(Max. 40 words)");
		this.setForeground(Color.GRAY);		
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(i){
			this.setText(""); // Removes the placeholder for the user to enter their own text when selected		
			this.setForeground(Color.BLACK); 
			i = false;
		}		
	}
	@Override
	public void focusLost(FocusEvent e) {
		// Do nothing		
	}
}
