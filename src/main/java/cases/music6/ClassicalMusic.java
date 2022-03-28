package cases.music6;

import framework.annotations.Component;

import javax.inject.Named;

//@Named
//@Named("classicalMusic")
@Component
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
