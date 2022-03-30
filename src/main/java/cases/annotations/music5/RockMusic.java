package cases.annotations.music5;

import framework.annotations.Component;

@Component
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
