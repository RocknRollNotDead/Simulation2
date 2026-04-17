package simulation.backend;

import simulation.OutForConsole;

public class Main {
    private static final long STEP_DELAY_MS = 500;

    public static void main(String[] args) {

        Simulation simulation = new Simulation();

        while (true){
            simulation.doMove();
            OutForConsole.print(simulation);

            try {
                Thread.sleep(STEP_DELAY_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
