package cases.music6;

import framework.annotation.Scope;

import javax.inject.Named;

//@Named
@Scope("prototype")
@Named("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
