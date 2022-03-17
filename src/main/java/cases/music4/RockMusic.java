package cases.music4;

import implementation.annotation.Scope;

import javax.inject.Named;

//@Named
//@Scope("prototype")
@Scope("thread")
@Named("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
