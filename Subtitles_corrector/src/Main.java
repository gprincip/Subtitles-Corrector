import javax.swing.SwingUtilities;

import subtitles_corrector.frames.MainFrame;

public class Main {

	public static void main(String args[]) {
		
		SwingUtilities.invokeLater(() -> {

				new MainFrame(600,150);
				
			});
		
	}
	
}