package simulation.web;

import java.util.List;

public class SimulationState {
    private List<EntityDTO> entities;
    private long cycle;
    private List<SimulationEvent> events;

    public SimulationState() {
    }

    public SimulationState(List<EntityDTO> entities, long cycle, List<SimulationEvent> events) {
        this.entities = entities;
        this.cycle = cycle;
        this.events = events;
    }

    public List<EntityDTO> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDTO> entities) {
        this.entities = entities;
    }

    public long getCycle() {
        return cycle;
    }

    public void setCycle(long cycle) {
        this.cycle = cycle;
    }

    public List<SimulationEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SimulationEvent> events) {
        this.events = events;
    }
}
