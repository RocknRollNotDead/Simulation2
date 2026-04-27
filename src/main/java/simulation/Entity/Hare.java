package simulation.Entity;

public class Hare extends PeacefulAnimal{

    private static final String SYMBOL = "\uD83D\uDC30"; // 🐰
    private static final int COUNT_LIFES_FIRST_TIME = 30;
    private static final int MAX_COUNT_LIFES = COUNT_LIFES_FIRST_TIME + 20;


    public Hare(int x, int y) {
        super(x, y);
    }


    @Override
    protected int getCountLifesFirstTime() {
        return COUNT_LIFES_FIRST_TIME;
    }

    @Override
    protected int getMaxCountLifes() {
        return MAX_COUNT_LIFES;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
