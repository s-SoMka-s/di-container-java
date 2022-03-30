package cases.annotations.music3;

import framework.annotations.Component;

@Component("classicalMusic")
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
