package entity;

import stateContexts.MicrowaveContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static enums.MicrowaveEvents.TIMER_TICK;
import static enums.MicrowaveEvents.TIMER_TIMES_OUT;

public class CookTimer {

    private final static int TICK_TIMER = 300;
    private final AtomicBoolean isPause;
    private final AtomicBoolean isNotEnd ;
    private final AtomicInteger timeToCook;
    private final MicrowaveContext microwaveContext;
    private final ExecutorService timerService;
    private final Runnable timerRunnable;

    public CookTimer(MicrowaveContext microwaveContext) {
        this.microwaveContext = microwaveContext;
        timeToCook = new AtomicInteger(0);
        timerService = Executors.newSingleThreadExecutor();
        isNotEnd = new AtomicBoolean(true);
        isPause = new AtomicBoolean(false);
        timerRunnable = () -> {
            while (isNotEnd.get()) {
                try {
                    Thread.sleep(TICK_TIMER);
                    double lastTime = timeToCook.get();
                    if (lastTime > 0 && !isPause.get()) {
                        double newTime = timeToCook.addAndGet(-TICK_TIMER);
                        if (newTime <= 0) {
                            microwaveContext.safeTrigger(TIMER_TIMES_OUT);
                            isPause.set(true);
                        }
                        else microwaveContext.safeTrigger(TIMER_TICK);
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
        isNotEnd.set(false);
    }

    public Integer getTimeToCook() {
        return timeToCook.get();
    }

    public void startTimer(Integer timer) {
        isPause.set(false);
        timeToCook.set(timer);
    }

    public void endTimer(){
        isPause.set(true);
        timeToCook.set(0);
    }

}
