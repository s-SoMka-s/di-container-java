package cases.music8;

import framework.annotations.Component;
import framework.annotations.Inject;

//@Named
//@Named("classicalMusic")
@Component("classicalMusic")
public class ClassicalMusic implements Music {
    @Inject("jazzMusic")
    JazzMusic jazzMusic;

    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
