package subtitles_corrector.threads;
import javax.swing.JScrollPane;

public class DelayedActionThread implements Runnable{

	private JScrollPane scrollPane;

	public DelayedActionThread(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}
	
	//TODO: waits 1.5sec for the content in the subtitles frame to be rendered
	//before sets the scrollbars at the beginning. Works for now, but eventually
	//this will need to be replaced with something more appropriate, where the
	//exact moment when rendering is finished could be known and then adjust scrollbars position
	@Override
	public void run() {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.getHorizontalScrollBar().setValue(0);

	}

}
