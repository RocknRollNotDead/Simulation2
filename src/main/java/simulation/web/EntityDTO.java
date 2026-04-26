package simulation.web;

public class EntityDTO {
    private int x;
    private int y;
    private String type;
    private String symbol;
    private String toString;

    public EntityDTO() {
    }

    public EntityDTO(int x, int y, String type, String symbol, String toString) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.symbol = symbol;
        this.toString = toString;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getToString() {
        return toString;
    }

    public void setToString(String toString) {
        this.toString = toString;
    }
}
