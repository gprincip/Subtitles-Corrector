package subtitles_corrector.model;

/**
 * Class representing information about start and end point of subtitle appearing on the screen
 * and the content of a subtitle (text that is being shown)
 * @author gavrilo
 *
 */
public class SubtitleUnit {
	
	private Integer index;
	private String timeFrom;
	private String timeTo;
	private String text;
	
	public SubtitleUnit() {}
	
	public SubtitleUnit(Integer index, String timeFrom, String timeTo, String text) {
		this.index = index;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		this.text = text;
	}
	
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}
	public String getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
