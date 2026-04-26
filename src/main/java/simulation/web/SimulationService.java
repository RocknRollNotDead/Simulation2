package simulation.web;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import simulation.backend.Simulation;
import simulation.backend.Position;
import simulation.Entity.Entity;
import simulation.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SimulationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Simulation simulation;
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;
    private List<SimulationEvent> recentEvents = new CopyOnWriteArrayList<>();
    private static final int MAX_EVENTS = 50;

    public SimulationService() {
        this.simulation = new Simulation();
    }

    public void start() {
        if (isRunning) {
            return;
        }
        
        isRunning = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) {
                simulation.doMove();
                
                // Получаем события eating из симуляции
                List<String> events = simulation.getEatingEvents();
                for (String event : events) {
                    addEvent("eating", event);
                }
                simulation.clearEatingEvents();

                // Отправляем состояние всем подключенным клиентам
                SimulationState state = buildState();
                messagingTemplate.convertAndSend("/topic/simulation", state);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void pause() {
        isRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void reset() {
        pause();
        this.simulation = new Simulation();
        recentEvents.clear();
        addEvent("reset", "Симуляция сброшена");
    }

    public void step() {
        simulation.doMove();
        
        // Получаем события eating из симуляции
        List<String> events = simulation.getEatingEvents();
        for (String event : events) {
            addEvent("eating", event);
        }
        simulation.clearEatingEvents();
    }

    public SimulationState getCurrentState() {
        return buildState();
    }

    public ConfigDTO getConfig() {
        return new ConfigDTO(simulation.getWidth(), simulation.getHeight());
    }

    private void addEvent(String type, String message) {
        recentEvents.add(0, new SimulationEvent(type, message));
        if (recentEvents.size() > MAX_EVENTS) {
            recentEvents.remove(recentEvents.size() - 1);
        }
    }

    private SimulationState buildState() {
        List<EntityDTO> entities = new ArrayList<>();

        for (Map.Entry<Position, Entity> entry : simulation.getObjsMap().entrySet()) {
            Position pos = entry.getKey();
            Entity entity = entry.getValue();

            entities.add(new EntityDTO(
                    pos.getX(),
                    pos.getY(),
                    entity.getClass().getSimpleName(),
                    entity.getSymbol(),
                    entity.toString()
            ));
        }

        return new SimulationState(entities, simulation.getCycle(), new ArrayList<>(recentEvents));
    }
}
