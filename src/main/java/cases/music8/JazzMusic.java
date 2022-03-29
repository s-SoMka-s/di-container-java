package cases.music8;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("jazzMusic")
public class JazzMusic implements Music {
    @Inject("rockMusic")
    RockMusic rockMusic;

    @Override
    public String getSong() {
        return "What a wonderful World";
    }
}
