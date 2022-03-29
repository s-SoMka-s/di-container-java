package cases.annotations.music3;

import framework.annotations.Component;

@Component("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
