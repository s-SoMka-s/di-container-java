package cases.music5;

import framework.annotations.Scope;

import javax.inject.Named;

//@Named
@Scope(framework.enums.Scope.THREAD)
@Named("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
