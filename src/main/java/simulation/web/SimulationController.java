package simulation.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import simulation.backend.Simulation;
import simulation.backend.Position;
import simulation.Entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        simulationService.start();
        return ResponseEntity.ok("Started");
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pause() {
        simulationService.pause();
        return ResponseEntity.ok("Paused");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        simulationService.reset();
        return ResponseEntity.ok("Reset");
    }

    @PostMapping("/step")
    public ResponseEntity<String> step() {
        simulationService.step();
        return ResponseEntity.ok("Step");
    }

    @GetMapping("/state")
    public SimulationState getState() {
        return simulationService.getCurrentState();
    }

    @GetMapping("/config")
    public ConfigDTO getConfig() {
        return simulationService.getConfig();
    }
}
