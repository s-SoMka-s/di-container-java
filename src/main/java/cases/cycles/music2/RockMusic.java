package cases.cycles.music2;

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

    public ClassicalMusic getClassicalMusic() {
        return classicalMusic;
    }

    public JazzMusic getJazzMusic() {
        return jazzMusic;
    }
}
