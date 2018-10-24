package controllers;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.call.ContextHandler;
import entity.Food;
import entity.Microwave;
import enums.MicrowaveStates;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import stateContexts.MicrowaveContext;
import views.GetEatView;
import views.MicrowaveView;

import static au.com.ds.ef.FlowBuilder.from;
import static au.com.ds.ef.FlowBuilder.on;
import static enums.MicrowaveEvents.*;
import static enums.MicrowaveStates.*;

public class MicrowaveController {

    private Microwave microwave;
    private MicrowaveView microwaveView;

    public MicrowaveController(MicrowaveContext microwaveContext,MicrowaveView microwaveView) {
        microwave = new Microwave(microwaveContext);
        this.microwaveView = microwaveView;
        initStateMachine(microwaveContext);
        initViewTriggersToStateMachineEvents(microwaveContext);
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
        });
        microwaveFlow.whenEnter(READY_TO_COOK, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            Platform.runLater(() -> {
                GetEatView getEatView = new GetEatView();
                getEatView.showAndWait(food -> {
                    microwave.setFood(food);
                    microwaveView.microwaveToReadyToCook();
                });
            });
            microwaveView.microwaveToReadyToCook();
        });
        microwaveFlow.whenEnter(COOKING, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwave.startCook();
            setTimeToCook(microwave.getTimeToCook());
            microwaveView.microwaveToCooking();
        });

        timeToCookLog(microwaveFlow, TEMP_COOKING1);
        timeToCookLog(microwaveFlow, TEMP_COOKING2);

        microwaveFlow.whenEnter(COOKING_INTERRUPTED, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwaveView.microwaveToInterrupted();
        });
        microwaveFlow.whenEnter(COOKING_COMPLETE, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            microwaveView.microwaveToCookingComplete();
            setTimeToCook(0);
        });

    }

    private void timeToCookLog(EasyFlow<MicrowaveContext> microwaveFlow, MicrowaveStates tempCooking1) {
        microwaveFlow.whenEnter(tempCooking1, (ContextHandler<MicrowaveContext>) flowContext -> {
            logStateMachine(flowContext);
            Integer timeToCook = microwave.getTimeToCook();
            if(timeToCook>=0)
                setTimeToCook(timeToCook);
            microwaveView.microwaveToCooking();
        });
    }

    private void logStateMachine(MicrowaveContext flowContext) {
        System.out.println("STATE: " + flowContext.getState().name() + " LAST EVENT: " + flowContext.getLastEvent());
    }

    private EasyFlow<MicrowaveContext> initStates() {


        return from(EMPTY).transit(
                on(EAT_PUTED).to(READY_TO_COOK).transit(
                        on(BUTTON_PRESSED).to(COOKING).transit(
                                on(BUTTON_PRESSED).to(COOKING_INTERRUPTED).transit(
                                        on(BUTTON_PRESSED).to(COOKING),
                                        on(DOOR_TRIGGER).to(EMPTY)
                                ),
                                on(TIMER_TIMES_OUT).to(COOKING_COMPLETE).transit(
                                        on(DOOR_TRIGGER).to(EMPTY)
                                ),
                                on(TIMER_TICK).to(TEMP_COOKING1).transit(
                                        on(TIMER_TICK).to(TEMP_COOKING2).transit(
                                                on(TIMER_TICK).to(TEMP_COOKING1),
                                                on(TIMER_TIMES_OUT).to(COOKING_COMPLETE)
                                        ),
                                        on(TIMER_TIMES_OUT).to(COOKING_COMPLETE)
                                )
                        ),
                        on(DOOR_TRIGGER).to(EMPTY)
                )
        );
    }

    private void initViewTriggersToStateMachineEvents(MicrowaveContext microwaveContext){
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            double x = event.getX();
            double y = event.getY();

            if (x > 36 && x < 374 && y > 67 && y < 263) {
                microwaveContext.safeTrigger(EAT_PUTED);
                microwaveContext.safeTrigger(DOOR_TRIGGER);
            }
            if (x > 428 && x < 447 && y > 134 && y < 141) {
                microwaveContext.safeTrigger(BUTTON_PRESSED);
            }
        };
        microwaveView.setMouseEvent(mouseEventEventHandler);
    }

    private void setTimeToCook(Integer timeToCook){
        microwaveView.setTimeToCook(timeToCook);
    }

    public void stopApplication(){
        microwave.stopApplication();
    }
}
