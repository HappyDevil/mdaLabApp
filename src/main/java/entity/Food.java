package entity;

public class Food {

    private Integer timeToCook;
    private String name;


    public Food(Integer timeToCook, String name) {
        this.timeToCook = timeToCook;
        this.name = name;
    }

    public Integer getTimeToCook() {
        return timeToCook;
    }
}
