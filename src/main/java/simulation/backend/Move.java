package simulation.backend;

public record Move(int x, int y) {
    // в Animal должен возвращаться именно move как смещение позиции, а в Simulation обрабатываться именно Move и сама Simulation должна ставить Entity
}
