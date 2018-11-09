package entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import stateContexts.MicrowaveContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static enums.MicrowaveEvents.TIMER_TICK;
import static enums.MicrowaveEvents.TIMER_TIMES_OUT;

public class CookTimer {

    private final static int TICK_TIMER = 300;
    private final AtomicBoolean isPause;
    private final AtomicBoolean isNotEnd;
    private final IntegerProperty timeToCook;
    private final MicrowaveContext microwaveContext;
    private ExecutorService timerService;
    private final Runnable timerRunnable;

    public CookTimer(MicrowaveContext microwaveContext) {
        this.microwaveContext = microwaveContext;
        timeToCook = new SimpleIntegerProperty(0);
        timerService = Executors.newSingleThreadExecutor();
        isNotEnd = new AtomicBoolean(true);
        isPause = new AtomicBoolean(false);
        timerRunnable = () -> {
            while (isNotEnd.get()) {
                try {
                    Thread.sleep(TICK_TIMER);
                    double lastTime = timeToCook.get();
                    if (lastTime > 0 && !isPause.get()) {
                        int newTime = timeToCook.get() - TICK_TIMER;
                        timeToCook.set(newTime);
                        if (newTime <= 0) {
                            microwaveContext.safeTrigger(TIMER_TIMES_OUT);
                            isPause.set(true);
                        } else microwaveContext.safeTrigger(TIMER_TICK);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerService.submit(timerRunnable);
    }

    public void pause() {
        isPause.set(true);
    }

    public void unPause() {
        isPause.set(false);
    }

    public void stopAppAndTimer() {
        isPause.set(true);
        isNotEnd.set(false);
        timerService.shutdown();
    }

    public IntegerProperty getTimeToCook() {
        return timeToCook;
    }

    public void startTimer(Integer timer) {
        isPause.set(false);
        timeToCook.set(timer);
    }

    public void endTimer() {
        isPause.set(true);
        timeToCook.set(0);
    }

}
