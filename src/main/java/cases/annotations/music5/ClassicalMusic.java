package cases.annotations.music5;

import framework.annotations.Component;

@Component
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
