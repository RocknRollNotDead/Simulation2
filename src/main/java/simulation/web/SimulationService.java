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
import java.util.concurrent.*;

@Service
public class SimulationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Хранилище симуляций для каждого пользователя
    private final Map<String, UserSimulation> simulations = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    
    // Класс для хранения данных симуляции пользователя
    private static class UserSimulation {
        Simulation simulation;
        boolean isRunning;
        List<SimulationEvent> recentEvents;
        long lastAccessTime;
        
        UserSimulation() {
            this.simulation = new Simulation();
            this.isRunning = false;
            this.recentEvents = new CopyOnWriteArrayList<>();
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        void updateAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    private static final int MAX_EVENTS = 50;

    public SimulationService() {
        // Глобальный планировщик для всех симуляций
        scheduler = Executors.newScheduledThreadPool(4);
        
        // Запускаем задачу обновления всех активных симуляций
        scheduler.scheduleAtFixedRate(this::updateAllSimulations, 0, 500, TimeUnit.MILLISECONDS);
        
        // Очистка неактивных симуляций каждые 5 минут
        scheduler.scheduleAtFixedRate(this::cleanupInactiveSimulations, 5, 5, TimeUnit.MINUTES);
    }
    
    private UserSimulation getOrCreateSimulation(String userId) {
        UserSimulation sim = simulations.computeIfAbsent(userId, id -> new UserSimulation());
        sim.updateAccessTime();
        return sim;
    }
    
    private void updateAllSimulations() {
        simulations.forEach((userId, userSim) -> {
            if (userSim.isRunning) {
                userSim.simulation.doMove();
                
                // Получаем события из симуляции
                List<String> events = userSim.simulation.getEatingEvents();
                for (String event : events) {
                    if (event.contains("умер")) {
                        addEventToUser(userSim, "death", event);
                    } else if (event.contains("съел")) {
                        addEventToUser(userSim, "eating", event);
                    } else {
                        addEventToUser(userSim, "info", event);
                    }
                }
                userSim.simulation.clearEatingEvents();

                // Отправляем состояние только этому пользователю
                SimulationState state = buildState(userSim);
                messagingTemplate.convertAndSend("/topic/simulation/" + userId, state);
            }
        });
    }
    
    private void cleanupInactiveSimulations() {
        // Удаляем симуляции, которые не активны более 30 минут
        long thirtyMinutesAgo = System.currentTimeMillis() - (30 * 60 * 1000);
        simulations.entrySet().removeIf(entry -> 
            entry.getValue().lastAccessTime < thirtyMinutesAgo
        );
    }

    public void start(String userId) {
        UserSimulation userSim = getOrCreateSimulation(userId);
        userSim.isRunning = true;
    }

    public void pause(String userId) {
        UserSimulation userSim = getOrCreateSimulation(userId);
        userSim.isRunning = false;
    }

    public void reset(String userId) {
        UserSimulation userSim = getOrCreateSimulation(userId);
        userSim.isRunning = false;
        userSim.simulation = new Simulation();
        userSim.recentEvents.clear();
        addEventToUser(userSim, "reset", "Симуляция сброшена");
    }

    public void step(String userId) {
        UserSimulation userSim = getOrCreateSimulation(userId);
        userSim.simulation.doMove();
        
        List<String> events = userSim.simulation.getEatingEvents();
        for (String event : events) {
            if (event.contains("умер")) {
                addEventToUser(userSim, "death", event);
            } else if (event.contains("съел")) {
                addEventToUser(userSim, "eating", event);
            } else {
                addEventToUser(userSim, "info", event);
            }
        }
        userSim.simulation.clearEatingEvents();
    }

    public SimulationState getCurrentState(String userId) {
        UserSimulation userSim = getOrCreateSimulation(userId);
        return buildState(userSim);
    }

    public ConfigDTO getConfig() {
        return new ConfigDTO(Config.getWidth(), Config.getHeigh());
    }

    private void addEventToUser(UserSimulation userSim, String type, String message) {
        userSim.recentEvents.add(0, new SimulationEvent(type, message));
        if (userSim.recentEvents.size() > MAX_EVENTS) {
            userSim.recentEvents.remove(userSim.recentEvents.size() - 1);
        }
    }

    private SimulationState buildState(UserSimulation userSim) {
        List<EntityDTO> entities = new ArrayList<>();

        for (Map.Entry<Position, Entity> entry : userSim.simulation.getObjsMap().entrySet()) {
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

        return new SimulationState(entities, userSim.simulation.getCycle(), new ArrayList<>(userSim.recentEvents));
    }
}
