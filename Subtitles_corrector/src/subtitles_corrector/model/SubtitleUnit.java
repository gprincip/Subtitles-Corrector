package subtitles_corrector.model;

/**
 * Class representing information about start and end point of subtitle appearing on the screen
 * and the content of a subtitle (text that is being shown)
 * @author gavrilo
 *
 */
public class SubtitleUnit {
	
	/** Index of the subtitle as specified in the subtitles file */
	private Integer subtitleIndex;
	/** Index of the subtitle row in the subtitles grid in editor */
	private Integer rowIndex;
	private String timeFrom;
	private String timeTo;
	private String text;
	
	public SubtitleUnit() {}
	
	public SubtitleUnit(Integer rowIndex, Integer subtitleIndex, String timeFrom, String timeTo, String text) {
		this.rowIndex = rowIndex;
		this.subtitleIndex = subtitleIndex;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		this.text = text;
	}
	
	
	public Integer getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
	}
	public Integer getSubtitleIndex() {
		return subtitleIndex;
	}
	public void setIndex(Integer subtitleIndex) {
		this.subtitleIndex = subtitleIndex;
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
