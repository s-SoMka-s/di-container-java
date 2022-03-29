package cases.cycles.music1;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("classicalMusic")
public class ClassicalMusic implements Music {
    @Inject("rockMusic")
    RockMusic rockMusic;

    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }

    public RockMusic getRockMusic() {
        return rockMusic;
    }
}
