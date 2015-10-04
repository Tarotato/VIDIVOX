package VIDIVOX_prototype;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

public class CommentaryTextArea extends JTextArea implements FocusListener{
	
	boolean i = true;
	
	public CommentaryTextArea(){
		this.addFocusListener(this);
		// Placeholder for when VIDIVOX is first run
		this.setText("Enter your commentary here"+ System.getProperty("line.separator") +"(Max. 40 words)");
		this.setForeground(Color.GRAY);		
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Removes the placeholder for the user to enter their own text when selected
		// This occurs only once when VIDIVOX is first run
		if(i){
			this.setText("");
			this.setForeground(Color.BLACK);
			i = false;
		}		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Do nothing		
	}
}
