package subtitles_corrector.model;

import javax.swing.JButton;

/**
 * Class that will hold additional information about JButton
 * @author gavrilo
 *
 */
public class ButtonWrapper{

	private JButton button;
	/** Button's <strong>0-based</strong> index related to the vertical position in the subtitles grid</br>*/
	private Integer index;
	
	public ButtonWrapper() {}
	
	public ButtonWrapper(JButton button, Integer index) {
		this.button = button;
		this.index = index;
	}
	
	public JButton getButton() {
		return button;
	}
	public void setButton(JButton button) {
		this.button = button;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}

}
