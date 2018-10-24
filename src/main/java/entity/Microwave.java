package entity;

import stateContexts.MicrowaveContext;

public class Microwave {

    private Food currentFood;
    private CookTimer cookingCookTimer;

    public Microwave(MicrowaveContext microwaveContext) {
        this.cookingCookTimer = new CookTimer(microwaveContext);
    }

    public Boolean isCooking() {
        return cookingCookTimer.getTimeToCook() > 0;
    }

    public Integer getTimeToCook() {
        return cookingCookTimer.getTimeToCook();
    }

    public Food getCurrentFood() {
        return currentFood;
    }

    public void setFood(Food food) {
        currentFood = food;
    }

    public void startCook(){
        cookingCookTimer.startTimer(currentFood.getTimeToCook());
    }
    public void stopApplication() {
        cookingCookTimer.stopAppAndTimer();
    }
}
