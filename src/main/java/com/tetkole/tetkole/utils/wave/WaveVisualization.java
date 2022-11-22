/*
 * 
 */
package com.tetkole.tetkole.utils.wave;

import com.tetkole.tetkole.utils.wave.WaveFormService.WaveFormJob;
import javafx.animation.AnimationTimer;
import javafx.scene.input.ScrollEvent;

/**
 * The Class Visualizer.
 *
 * @author GOXR3PLUS
 */
public class WaveVisualization extends WaveFormPane {
	
	/*** This Service is constantly repainting the wave */
	private final PaintService animationService;
	
	/*** This Service is creating the wave data for the painter */
	private final WaveFormService waveService;

	
	/**
	 * Constructor
	 */
	public WaveVisualization() {
		super(500, 500);
		waveService = new WaveFormService(this);
		animationService = new PaintService();


		// ----------
		widthProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Width is:" + newValue);
			
			// Canvas Width
			this.width = Math.round(newValue.floatValue());
			
			//Draw single line :)
			if (getWaveService().getResultingWaveform() != null)
				getWaveService().startService(getWaveService().getFileAbsolutePath(), WaveFormJob.WAVEFORM);
			clear();
			
		});
		// -------------
		heightProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Height is:" + newValue);
			
			// Canvas Height
			this.height = Math.round(newValue.floatValue());
			
			//Draw single line :)
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
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * Stars the wave visualiser painter
	 */
	public void startPainterService() {
		animationService.start();
	}
	
	/**
	 * Stops the wave visualiser painter
	 */
	public void stopPainterService() {
		animationService.stop();
		clear();
	}

	public void setCursorTime(double seconds) {
		setCurrentXPosition((seconds * this.width) / totalTime);
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
		this.totalTime = seconds;
		this.setEndAudio(seconds);
		// set up number of wave for the audio, 200 wave per seconds
		this.waveService.setArrayWaveLength((int)this.totalTime * 200);
	}

	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							      Paint Service
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	/**
	 * This Service is updating the visualizer.
	 *
	 * @author GOXR3PLUS
	 */
	public class PaintService extends AnimationTimer {
		
		@Override
		public void handle(long nanos) {
			//Paint
			paintWaveForm();
		}
		
		@Override
		public void stop() {
			super.stop();
		}
	}
	
}
