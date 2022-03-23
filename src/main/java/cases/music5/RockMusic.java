package cases.music5;

import framework.annotation.Scope;

import javax.inject.Named;

//@Named
@Scope("thread")
@Named("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
