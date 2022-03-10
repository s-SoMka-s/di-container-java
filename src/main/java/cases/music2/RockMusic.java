package cases.music2;

import javax.inject.Named;

//@Named
@Named("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Highway to Hell";
    }
}
