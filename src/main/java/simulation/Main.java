package simulation;

import simulation.backend.Simulation;
import simulation.other.Config;

public class Main {
    private static final long STEP_DELAY_MS = 500;

    public static void main(String[] args) {

        Simulation simulation = new Simulation(Config.getWidth(), Config.getHeigh());
        OutForConsole renderer = new OutForConsole();
        while (true){
            simulation.doMove();
            renderer.print(simulation);

            try {
                Thread.sleep(STEP_DELAY_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
