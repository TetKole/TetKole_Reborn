/*
 * 
 */
package com.tetkole.tetkole.utils.wave;

import com.tetkole.tetkole.utils.wave.WaveFormService.WaveFormJob;
import javafx.animation.AnimationTimer;

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

	private double totalTime;
	
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
		
		//Tricky mouse events
		setOnMouseMoved(m -> this.setMouseXPosition((int) m.getX()));
		setOnMouseDragged(m -> this.setMouseXPosition((int) m.getX()));
		setOnMouseExited(m -> this.setMouseXPosition(-1));
	}
	
	public WaveFormService getWaveService() {
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
		setTimerXPosition((int) ((seconds * this.width) / totalTime));
	}

	public void setTotalTime(double seconds) {
		this.totalTime = seconds;
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
		public void start() {
			// Values must be >0
			if (width <= 0 || height <= 0)
				width = height = 1;
			
			super.start();
		}
		
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
