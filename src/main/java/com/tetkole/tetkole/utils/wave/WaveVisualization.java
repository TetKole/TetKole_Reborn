/*
 * 
 */
package com.tetkole.tetkole.utils.wave;

import com.tetkole.tetkole.utils.StaticEnvVariable;
import com.tetkole.tetkole.utils.wave.WaveFormService.WaveFormJob;
import javafx.animation.AnimationTimer;
import javafx.scene.input.ScrollEvent;

/**
 * The Class Visualizer.
 *
 * @author GOXR3PLUS
 */
public class WaveVisualization extends WaveFormPane {

	/*** This Service is creating the wave data for the painter */
	private final WaveFormService waveService;

	
	/**
	 * Constructor
	 */
	public WaveVisualization() {
		super(500, 500);
		waveService = new WaveFormService(this);

		// ----------
		widthProperty().addListener((observable , oldValue , newValue) -> {
			
			// Canvas Width
			this.setWidthPane(newValue.intValue());
			
			// Draw single line
			if (getWaveService().getResultingWaveform() != null)
				getWaveService().startService(getWaveService().getFileAbsolutePath(), WaveFormJob.WAVEFORM);
			this.setPositionBorderWithTime();
			clear();
			
		});
		// -------------
		heightProperty().addListener((observable , oldValue , newValue) -> {
			
			// Canvas Height
			this.setHeightPane(newValue.intValue());
			
			//Draw single line
			if (getWaveService().getResultingWaveform() != null)
				getWaveService().startService(getWaveService().getFileAbsolutePath(), WaveFormJob.WAVEFORM);
			clear();
		});

	}

	public void startVisualization(String fileAbsolutePath, WaveFormJob waveFormJob) {
		getWaveService().startService(fileAbsolutePath, waveFormJob);
	}
	
	private WaveFormService getWaveService() {
		return waveService;
	}

	public void setCursorTime(double seconds) {
		setCurrentXPosition((seconds - this.getBeginAudio()) * this.getRatioAudio());
		paintWaveForm();
	}

	public void setRangeZoom(ScrollEvent scrollEvent) {
		// we zoom the wave when we scroll
		if (scrollEvent.getDeltaY() > 0) {
			this.waveService.setWaveRange(this.getLeftBorderXPosition(), this.getRightBorderXPosition(), this.width);
			this.setAudioRange();
		} else {
			this.waveService.resetWaveRange();
			this.resetAudioRange();
		}
		getWaveService().startService(getWaveService().getFileAbsolutePath(), WaveFormJob.WAVEFORM);
		clear();
	}

	public void setTotalTime(double seconds) {
		this.initTotalTime(seconds);
		// set up number of wave for the audio, 200 wave per seconds
		//TODO change array waveLenght to upgrade zoom
		this.waveService.setArrayWaveLength((int)this.totalTime * StaticEnvVariable.zoomRange);
	}
	
}
