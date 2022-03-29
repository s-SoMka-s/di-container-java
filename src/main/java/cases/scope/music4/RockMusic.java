package cases.scope.music4;

import framework.annotations.Component;
import framework.annotations.Scope;

import static framework.enums.Scope.THREAD;

@Scope(THREAD)
@Component("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
