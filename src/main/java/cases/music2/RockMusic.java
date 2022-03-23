package cases.music2;

import framework.annotations.Scope;

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
