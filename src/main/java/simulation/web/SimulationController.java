package simulation.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @PostMapping("/start")
    public ResponseEntity<String> start(@RequestParam String userId) {
        simulationService.start(userId);
        return ResponseEntity.ok("Started");
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pause(@RequestParam String userId) {
        simulationService.pause(userId);
        return ResponseEntity.ok("Paused");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset(@RequestParam String userId) {
        simulationService.reset(userId);
        return ResponseEntity.ok("Reset");
    }

    @PostMapping("/step")
    public ResponseEntity<String> step(@RequestParam String userId) {
        simulationService.step(userId);
        return ResponseEntity.ok("Step");
    }

    @GetMapping("/state")
    public SimulationState getState(@RequestParam String userId) {
        return simulationService.getCurrentState(userId);
    }

    @GetMapping("/config")
    public ConfigDTO getConfig() {
        return simulationService.getConfig();
    }
}
