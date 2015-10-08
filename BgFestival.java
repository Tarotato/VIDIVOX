package vidivox_beta;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.SwingWorker;

/**
 * @author Isabel Zhuang and Rebecca Lee
 * Class executes festival text-to-speech from commentary entered by the user
 */
public class BgFestival extends SwingWorker<Void, Void> {

	protected int festID;
	private ArrayList<Integer> killPID = new ArrayList<Integer>();
	private String input;

	public BgFestival(String input, ArrayList<Integer> killPID) {
		this.input = input;
		this.killPID = killPID;
	}

	/* 
	 * Uses festival to speak out the commentary to the user
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		killPID.add(0);		
		try {	
			
			String cmd = "echo \"" + input + "\" | festival --tts & echo $!"; // echo $! returns the process ID of the process just executed

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
		
			// Read the process ID for the process executed above
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutbr = new BufferedReader(new InputStreamReader(stdout));
			String line = stdoutbr.readLine();
			festID = Integer.parseInt(line); // Stores ID for canceling(Stop)
			killPID.set(0, festID);

		} catch (Exception ex) {
				ex.printStackTrace();
		}
		return null;
	}	
}

