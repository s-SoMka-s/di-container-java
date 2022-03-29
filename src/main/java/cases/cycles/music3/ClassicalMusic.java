package cases.cycles.music3;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("classicalMusic")
public class ClassicalMusic implements Music {
    @Inject("jazzMusic")
    JazzMusic jazzMusic;

    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }

    public JazzMusic getJazzMusic() {
        return jazzMusic;
    }
}
