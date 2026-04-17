package simulation.Entity;

public abstract class Edible extends EnvironmentObject {
    /** Этот класс нужен только для того, чтобы животные могли определять можно ли скушать или нет */
    public Edible(int x, int y) {
        super(x, y);
    }
}
