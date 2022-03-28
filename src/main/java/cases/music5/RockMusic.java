package cases.music5;

import framework.annotations.Component;
import framework.annotations.Scope;

import javax.inject.Named;

import static framework.enums.Scope.THREAD;

//@Scope(THREAD)
@Component("rockMusic")
//@Component
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
