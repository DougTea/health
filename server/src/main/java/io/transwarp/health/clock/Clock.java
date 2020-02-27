package io.transwarp.health.clock;

import java.util.Timer;
import java.util.TimerTask;

public class Clock {

  static int clockUpdateIntervalMs = 5;

  volatile long timeInMills = System.currentTimeMillis();
  private Timer timer = new Timer(true);

  public Clock() {
    schedule(new UpdateClock(), clockUpdateIntervalMs);
  }

  class UpdateClock extends TimerTask {
    @Override
    public void run() {
      timeInMills = System.currentTimeMillis();
      schedule(new UpdateClock(), clockUpdateIntervalMs);
    }
  }

  public Long currentTimeMillis() {
    return timeInMills;
  }

  private void schedule(TimerTask task, int intervalMS) {
    timer.schedule(task, intervalMS);
  }

  void shutdown() {
    timer.cancel();
  }
}
