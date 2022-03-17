package cases.music5;

import javax.inject.Named;

//@Named
@Named("classicalMusic")
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
