package com.game.legend.net;

import java.util.Timer;
import java.util.TimerTask;

public class HeartBeat {

	private static int MAX_DELAY = 20;

	private Timer timer;

	private Boolean hasPong = true;

	private Runnable handler;

	public HeartBeat() {
		this.timer = new Timer(true);
		this.timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				if (handler != null) {
					handler.run();
				}
			};

		}, 0, 1000 * MAX_DELAY);
	}
	
	public void setHandler(Runnable r){
		this.handler = r;
	}

	public void stop() {
		timer.cancel();
	}

	public void pong() {
		this.hasPong = true;
	}
}
