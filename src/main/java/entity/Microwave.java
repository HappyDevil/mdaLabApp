package entity;

import javafx.beans.property.IntegerProperty;
import stateContexts.MicrowaveContext;

public class Microwave {

    private Food currentFood;
    private CookTimer cookingCookTimer;

    public Microwave(MicrowaveContext microwaveContext) {
        this.cookingCookTimer = new CookTimer(microwaveContext);
    }

    public IntegerProperty getTimeToCook() {
        return cookingCookTimer.getTimeToCook();
    }

    public Food getCurrentFood() {
        return currentFood;
    }

    public void setFood(Food food) {
        currentFood = food;
    }

    public void startCook(){
        Integer timeToCook = currentFood.getTimeToCook();
        cookingCookTimer.startTimer(timeToCook);
    }


    public void pauseCook(){
        cookingCookTimer.pause();
    }

    public void stopApplication() {
        cookingCookTimer.stopAppAndTimer();
    }

    public void startCook(int eatTimeCook) {
        cookingCookTimer.startTimer(eatTimeCook);
    }

    public void unPause() {
        cookingCookTimer.unPause();
    }
}
