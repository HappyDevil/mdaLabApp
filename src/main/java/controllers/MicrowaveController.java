package controllers;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.call.ContextHandler;
import entity.Food;
import entity.Microwave;
import enums.MicrowaveStates;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import stateContexts.MicrowaveContext;
import utils.ResourseUtils;
import views.MicrowaveView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static au.com.ds.ef.FlowBuilder.from;
import static au.com.ds.ef.FlowBuilder.on;
import static enums.MicrowaveEvents.*;
import static enums.MicrowaveStates.*;

public class MicrowaveController {

    private Microwave microwave;
    private MicrowaveView microwaveView;
    private boolean eatPuted = false;
    private int eatTimeCook = 0;
    private ExecutorService stateMachine_executor;

    public MicrowaveController(MicrowaveContext microwaveContext, MicrowaveView microwaveView) {
        microwave = new Microwave(microwaveContext);
        this.microwaveView = microwaveView;
        stateMachine_executor = Executors.newSingleThreadExecutor();
        initStateMachine(microwaveContext);
        initViewTriggersToStateMachineEvents(microwaveContext);
        bindTimeToCook(microwave.getTimeToCook());
    }

    private void initStateMachine(MicrowaveContext microwaveContext) {
        EasyFlow<MicrowaveContext> microwaveFlow = initStates();
        initEvents(microwaveFlow);
        microwaveFlow.start(microwaveContext);
    }

    private void initEvents(EasyFlow<MicrowaveContext> microwaveFlow) {
        microwaveFlow.whenEnter(EMPTY, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwaveView.microwaveToEmpty();
            eatPuted = false;
            setTimeCookMills(0);
        });
        microwaveFlow.whenEnter(READY_TO_COOK, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwave.setFood(new Food(5000, "Chicken"));
            microwaveView.microwaveToReadyToCook();
            eatPuted = true;
            eatTimeCook = 0;
        });
        microwaveFlow.whenEnter(COOKING, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            if (eatTimeCook > 0) {
                microwave.startCook(eatTimeCook);
                eatTimeCook = 0;
            }
            else microwave.startCook();
            eatPuted = false;
            microwaveView.microwaveToCooking();
        });

        timeToCookLog(microwaveFlow, TEMP_COOKING1);
        timeToCookLog(microwaveFlow, TEMP_COOKING2);

        microwaveFlow.whenEnter(COOKING_INTERRUPTED, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwave.pauseCook();
            microwaveView.microwaveToInterrupted();
        });
        microwaveFlow.whenEnter(COOKING_COMPLETE, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwaveView.microwaveToCookingComplete();
        });

    }

    private void timeToCookLog(EasyFlow<MicrowaveContext> microwaveFlow, MicrowaveStates tempCooking1) {
        microwaveFlow.whenEnter(tempCooking1, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwave.unPause();
            microwaveView.microwaveToCooking();

        });
    }

    private void logStateMachine(MicrowaveContext flowContext) {
        Platform.runLater(() -> microwaveView.setState("STATE: " + flowContext.getState().name() + "\n LAST EVENT: " + flowContext.getLastEvent()));
    }

    private EasyFlow<MicrowaveContext> initStates() {
        return from(EMPTY).transit(
                on(EAT_PUTED).to(READY_TO_COOK).transit(
                        on(BUTTON_PRESSED).to(COOKING).transit(
                                on(BUTTON_PRESSED).to(COOKING_INTERRUPTED).transit(
                                        on(BUTTON_PRESSED).to(TEMP_COOKING1),
                                        on(DOOR_TRIGGER).to(EMPTY)
                                ),
                                on(TIMER_TIMES_OUT).to(COOKING_COMPLETE).transit(
                                        on(DOOR_TRIGGER).to(EMPTY)
                                ),
                                on(TIMER_TICK).to(TEMP_COOKING1).transit(
                                        on(TIMER_TICK).to(TEMP_COOKING2).transit(
                                                on(TIMER_TICK).to(TEMP_COOKING1),
                                                on(TIMER_TIMES_OUT).to(COOKING_COMPLETE),
                                                on(BUTTON_PRESSED).to(COOKING_INTERRUPTED)
                                        ),
                                        on(TIMER_TIMES_OUT).to(COOKING_COMPLETE),
                                        on(BUTTON_PRESSED).to(COOKING_INTERRUPTED)
                                )
                        ),
                        on(DOOR_TRIGGER).to(EMPTY)
                )
        ).executor(stateMachine_executor);
    }

    private void initViewTriggersToStateMachineEvents(MicrowaveContext microwaveContext) {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            double x = event.getX();
            double y = event.getY();

            int startXDoor = 36;
            int lengthXDoor = 338;
            int startYDoor = 84;
            int lengthYDoor = 181;

            int startXStartButton = 428;
            int lengthXStartButton = 19;
            int startYStartButton = 133;
            int lengthYStartButton = 9;

            int lengthXNumberButton = 15;
            int lengthYNumberButton = 10;
            int lengthXWithoutButton = 4;
            int lengthYWithoutButton = 5;
            if (x > startXDoor && x < startXDoor + lengthXDoor && y > startYDoor && y < startYDoor + lengthYDoor) {
                microwaveContext.safeTrigger(EAT_PUTED);
                microwaveContext.safeTrigger(DOOR_TRIGGER);
            }
            if (x > startXStartButton && x < startXStartButton + lengthXStartButton && y > startYStartButton && y < startYStartButton + lengthYStartButton) {
                microwaveContext.safeTrigger(BUTTON_PRESSED);
            }
            if (eatPuted && x > 408 && y > 205 && x < 466 && y < 247) {
                int buttonStartX = (int) (x - 408);
                int buttonStartY = (int) (y - 205);

                int xNumberButton = getNumberButtonByCoord(buttonStartX, lengthXNumberButton, lengthXWithoutButton);
                int yNumberButton = getNumberButtonByCoord(buttonStartY, lengthYNumberButton, lengthYWithoutButton);

                int buttonValue = (xNumberButton + (yNumberButton - 1) * 3);
                this.eatTimeCook += buttonValue * 1000;
                this.eatTimeCook = Math.min(this.eatTimeCook, 5999 * 1000);
                setTimeCookMills(this.eatTimeCook);
            }
        };
        microwaveView.setMouseEvent(mouseEventEventHandler);
    }

    private int getNumberButtonByCoord(int buttonStart, int lengthNumberButton, int lengthWithoutButton) {
        int numberButton = 0;
        if (buttonStart < lengthNumberButton) numberButton = 1;
        else if (buttonStart < lengthNumberButton + lengthWithoutButton) return numberButton;
        else if (buttonStart < lengthNumberButton * 2 + lengthWithoutButton) numberButton = 2;
        else if (buttonStart < lengthNumberButton * 2 + lengthWithoutButton * 2) return numberButton;
        else numberButton = 3;
        return numberButton;
    }

    private void bindTimeToCook(IntegerProperty timeToCook) {
        timeToCook.addListener((observable, oldValue, newValue) -> {
            int newTimeToCook = newValue.intValue();
            setTimeCookMills(newTimeToCook);
        });
    }

    private void setTimeCookMills(int newTimeToCook) {
        String timeToCookStr = (newTimeToCook > 0) ? ResourseUtils.convertMillisecondsToMicrowaveTimestamp(newTimeToCook) : "00:00";
        Platform.runLater(() -> microwaveView.setTimeToCook(timeToCookStr));
    }

    public void stopApplication() {
        stateMachine_executor.shutdownNow();
        microwave.stopApplication();
    }
}
