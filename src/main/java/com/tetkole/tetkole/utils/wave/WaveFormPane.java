package com.tetkole.tetkole.utils.wave;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Swing panel paints the waveform of a track.
 *
 * @author GOXR3PLUS STUDIO
 */
public class WaveFormPane extends ResizableCanvas {

	private final float[] defaultWave;
	private float[] waveData;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color transparentForeground;
	private final Color mouseXColor = Color.rgb(255, 255, 255, 0.7);
	int width;
	int height;
	private double currentXPosition = 0;
	private double mouseXPosition = -1;
	private double leftBorderXPosition;
	private boolean leftBorderDragged = false;
	private double leftBorderTime;
	private double rightBorderXPosition;
	private boolean rightBorderDragged = false;
	private double rightBorderTime;
	protected double totalTime;
	private PropertyChangeSupport support;
	private final GraphicsContext gc = getGraphicsContext2D();
	private double beginAudio = 0;
	private double endAudio;

	/**
	 * Constructor
	 *
	 * @param width
	 * @param height
	 */
	public WaveFormPane(int width, int height) {
		defaultWave = new float[width];
		this.width = width;
		this.height = height;
		this.setWidth(width);
		this.setHeight(height);

		this.leftBorderXPosition = 0;
		this.rightBorderXPosition = (2 * width) / 3.0;

		//Create the default Wave
		for (int i = 0; i < width; i++)
			defaultWave[i] = 0.28802148f;
		waveData = defaultWave;

		backgroundColor = Color.web("#252525");
		setForeground(Color.ORANGE);


		// we look for a click on left border or right border
		setOnMousePressed(event -> {
			// click on left border
			if (event.getX() >= leftBorderXPosition && event.getX() <= leftBorderXPosition + 10) {
				leftBorderDragged = true;
			}

			// click on right border
			if (event.getX() >= rightBorderXPosition && event.getX() <= rightBorderXPosition + 10) {
				rightBorderDragged = true;
			}
		});

		// we drag a border if it was clicked
		setOnMouseDragged(event -> {
			setMouseXPosition((int) event.getX());

			if (rightBorderDragged) {
				rightBorderXPosition = Math.min(Math.max(event.getX(), leftBorderXPosition + 10),  getWidth() - 10);
				this.setRightBorderTime(rightBorderXPosition * this.getRatioAudio() + this.beginAudio);
			}

			if (leftBorderDragged) {
				leftBorderXPosition = Math.max(Math.min(event.getX(), rightBorderXPosition - 10), 0);
				this.setLeftBorderTime(leftBorderXPosition * this.getRatioAudio() + this.beginAudio);
			}
		});

		// we disabled draggable border and set the new currentXPosition to the left border
		setOnMouseReleased(event -> {
			// if one of the border is dragged --> set current to left border
			if (leftBorderDragged || rightBorderDragged) {
				this.setCurrentXPositionMediaPlayer(leftBorderXPosition);
			} else { // else set current to click position
				if (event.getX() > leftBorderXPosition + 10 && event.getX() < rightBorderXPosition) {
					this.setCurrentXPositionMediaPlayer(mouseXPosition);
				}
			}

			leftBorderDragged = false;
			rightBorderDragged = false;
		});

		// we set the mouseXPosition on hover
		setOnMouseMoved(event -> {
			setMouseXPosition(event.getX());
		});

		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}
	public void setCurrentXPositionMediaPlayer(double newValue) {
		support.firePropertyChange("currentXPosition", this.currentXPosition, newValue);
		this.currentXPosition = newValue;
	}

	public double getRightBorderXPosition() {
		return rightBorderXPosition;
	}

	/**
	 * Set the WaveData
	 *
	 * @param waveData
	 */
	public void setWaveData(float[] waveData) {
		this.waveData = waveData;
	}

	public void setForeground(Color color) {
		this.foregroundColor = color;
		transparentForeground = Color.rgb((int) ( foregroundColor.getRed() * 255 ), (int) ( foregroundColor.getGreen() * 255 ), (int) ( foregroundColor.getBlue() * 255 ), 0.3);
	}

	public double getCurrentXPosition() {
		return currentXPosition;
	}

	public void setCurrentXPosition(double currentXPosition) {
		this.currentXPosition = currentXPosition;
	}

	public void setMouseXPosition(double mouseXPosition) {
		this.mouseXPosition = mouseXPosition;
	}

	public double getLeftBorderXPosition() {
		return leftBorderXPosition;
	}

	public double getBeginAudio() {
		return beginAudio;
	}

	public double getLeftBorderTime() {
		return leftBorderTime;
	}

	public void setLeftBorderTime(double leftBorderTime) {
		this.leftBorderTime = leftBorderTime;
	}

	public double getRightBorderTime() {
		return rightBorderTime;
	}

	public void setRightBorderTime(double rightBorderTime) {
		this.rightBorderTime = rightBorderTime;
	}

	public void setEndAudio(double endAudio) {
		this.endAudio = endAudio;
	}

	public double getRatioAudio(){
		return this.getWidth() / (this.endAudio - this.beginAudio);
	}

	public void initTotalTime(double seconds){
		this.totalTime = seconds;
		this.endAudio = seconds;
		this.leftBorderXPosition = (seconds * 0.1) * this.getRatioAudio();
		this.rightBorderXPosition = (seconds * 0.9) * this.getRatioAudio();
		this.setLeftBorderTime(seconds * 0.1);
		this.setRightBorderTime(seconds * 0.9);
		this.setCurrentXPositionMediaPlayer(leftBorderXPosition);
	}

	public void setAudioRange(){
		double newBeginAudio = this.leftBorderXPosition / this.getRatioAudio() + this.beginAudio;
		double newEndAudio = this.rightBorderXPosition / this.getRatioAudio() + this.beginAudio;

		this.setLeftBorderTime((newEndAudio - newBeginAudio) * 0.1 + newBeginAudio);
		this.setRightBorderTime((newEndAudio - newBeginAudio) * 0.9 + newBeginAudio);

		this.beginAudio = Math.max(0 , newBeginAudio);
		this.endAudio = Math.min(newEndAudio, this.totalTime);
		this.leftBorderXPosition = this.getWidth() * 0.1;
		this.rightBorderXPosition = this.getWidth() * 0.9;
		this.setCurrentXPositionMediaPlayer(leftBorderXPosition);
	}

	public void resetAudioRange(){
		this.beginAudio = 0;
		this.endAudio = this.totalTime;
		this.leftBorderXPosition = this.getLeftBorderTime() * this.getRatioAudio();
		this.rightBorderXPosition = this.getRightBorderTime() * this.getRatioAudio();
		this.setCurrentXPositionMediaPlayer(leftBorderXPosition);
	}

	/**
	 * Clear the waveform
	 */
	public void clear() {
		waveData = defaultWave;

		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);

		//Paint a line
		gc.setStroke(foregroundColor);
		gc.strokeLine(0, height / 2.0, width, height / 2.0);
	}

	/**
	 * Paint the WaveForm
	 */
	public void paintWaveForm() {

		//Draw a Background Rectangle
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, width, height);

		//Draw the waveform
		gc.setStroke(foregroundColor);
		if (waveData != null) {
			for (int i = 0; i < waveData.length; i++) {
				int value = (int) (waveData[i] * height);
				int y1 = (height - 2 * value) / 2;
				int y2 = y1 + 2 * value;
				gc.strokeLine(i, y1, i, y2);
			}
		}

		// Draw Left Border
		gc.setFill(Color.WHITE);
		gc.fillRect(this.leftBorderXPosition, 0, 10, height);
		// Write the time at top of the border left border
		gc.strokeText(this.getTimeLeftBorderInString(), this.posLeftStrokeText(), adjustPosYStrokeText(), 500);

		// Draw Right Border
		gc.fillRect(this.rightBorderXPosition, 0, 10, height);
		// Write the time at top of the right border
		gc.strokeText(this.getTimeRightBorderInString(), this.posRightStrokeText(), 15, 500);

		// Draw Transparent Rect for borders
		gc.setFill(transparentForeground);
		gc.fillRect(0, 0, this.leftBorderXPosition, height);
		gc.fillRect((this.rightBorderXPosition + 10), 0, width, height);


		// Draw currentXPosition
		gc.setFill(Color.RED);
		gc.fillRect(currentXPosition, 0, 3, height);

		//Draw mouseXPositon
		if (mouseXPosition != -1) {
			gc.setFill(mouseXColor);
			gc.fillRect(mouseXPosition, 0, 3, height);
		}
	}



	/* the following function are used to manage text timestamp on border */

	public String getTimeLeftBorderInString() {
		double time = this.leftBorderXPosition / (this.getRatioAudio()) + this.beginAudio; //waveFormService.getRatioAudio();
		double milliTime = Math.round(time * 100.0) / 100.0;

		double hoursLeftBorderTime = time / 3600;
		double minutesLeftBorderTime = (time % 3600) / 60;
		double secondsLeftBorderTime = time % 60;

		String hours = String.valueOf(hoursLeftBorderTime);
		String onlyHours = hours.split("\\.")[0];
		String minutes = String.valueOf(minutesLeftBorderTime);
		String onlyMinutes = minutes.split("\\.")[0];
		String seconds = String.valueOf(secondsLeftBorderTime);
		String onlySeconds = seconds.split("\\.")[0];
		String milliSeconds = String.valueOf(milliTime);
		String onlyMilliSeconds = milliSeconds.substring(milliSeconds.indexOf(".")).substring(1);

		return onlyHours + "h:" + onlyMinutes + "min:" + onlySeconds + "s:" + onlyMilliSeconds + "ms";
	}

	public String getTimeRightBorderInString() {
		double time = this.rightBorderXPosition / this.getRatioAudio() + this.beginAudio;
		double milliTime = Math.round((this.rightBorderXPosition / this.getRatioAudio()) * 100.0) / 100.0;

		double hoursRightBorderTime = time / 3600;
		double minutesRightBorderTime = (time % 3600) / 60;
		double secondsRightBorderTime = time % 60;

		String hours = String.valueOf(hoursRightBorderTime);
		String onlyHours = hours.split("\\.")[0];
		String minutes = String.valueOf(minutesRightBorderTime);
		String onlyMinutes = minutes.split("\\.")[0];
		String seconds = String.valueOf(secondsRightBorderTime);
		String onlySeconds = seconds.split("\\.")[0];
		String milliSeconds = String.valueOf(milliTime);
		String onlyMilliSeconds = milliSeconds.substring(milliSeconds.indexOf(".")).substring(1);

		return onlyHours + "h:" + onlyMinutes + "min:" + onlySeconds + "s:" + onlyMilliSeconds + "ms";
	}

	public double posLeftStrokeText() {
		if (this.leftBorderXPosition < (getWidth() / 4)) {
			return this.leftBorderXPosition + 25;
		} else {
			return this.leftBorderXPosition - 110;
		}
	}

	public double posRightStrokeText() {
		if (this.rightBorderXPosition > ((getWidth() / 4) * 3)) {
			return this.rightBorderXPosition - 110;
		} else {
			return this.rightBorderXPosition + 25;
		}
	}

	/**
	 * This function is used to lower the Y position of left border's text when it collide with right border's text
	 * @return
	 */
	public int adjustPosYStrokeText() {
		if ((this.rightBorderXPosition - this.leftBorderXPosition) < 100.0) {
			return 30;
		} else {
			return 15;
		}
	}

}
