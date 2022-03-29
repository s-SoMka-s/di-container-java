package cases.music9;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("rockMusic")
public class RockMusic implements Music {
    @Inject("classicalMusic")
    ClassicalMusic classicalMusic;

    @Inject("jazzMusic")
    JazzMusic jazzMusic;

    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
