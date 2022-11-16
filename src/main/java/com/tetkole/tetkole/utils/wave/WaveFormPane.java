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
	private double rightBorderXPosition;
	private boolean rightBorderDragged = false;

	private PropertyChangeSupport support;

	private final GraphicsContext gc = getGraphicsContext2D();
	
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
			}

			if (leftBorderDragged) {
				leftBorderXPosition = Math.max(Math.min(event.getX(), rightBorderXPosition - 10), 0);
			}
		});

		// we disabled draggable border and set the new currentXPosition to the left border
		setOnMouseReleased(event -> {
			// if one of the border is dragged --> set current to left border
			if (leftBorderDragged || rightBorderDragged) {
				setCurrentXPositionMediaPlayer(leftBorderXPosition);
			} else { // else set current to click position
				if (event.getX() > leftBorderXPosition + 10 && event.getX() < rightBorderXPosition) {
					setCurrentXPositionMediaPlayer(mouseXPosition);
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
		// Write the time at top of the border
		//gc.strokeText(this.calculTimeLeftBorder(), this.posLeftStrokeText(), this.adjustPosYStrokeText(), 500);

		// Draw Right Border
		gc.fillRect(this.rightBorderXPosition, 0, 10, height);
		// Write the time at top of the border
		//gc.strokeText(this.calculTimeRightBorder(), this.posRightStrokeText(), 15, 500);

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
	
}
