package cases.music1;

import javax.inject.Named;

@Named("classicalMusic")
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
