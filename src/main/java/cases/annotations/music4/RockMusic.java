package cases.annotations.music4;

import framework.annotations.Component;

@Component("classicalMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
