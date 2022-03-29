package cases.scope.music2;

import framework.annotations.Component;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;

@Scope(PROTOTYPE)
@Component("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
