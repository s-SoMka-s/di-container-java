package cases.music5;

import framework.annotations.Component;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;

//@Named
//@Named("classicalMusic")
@Scope(PROTOTYPE)
@Component("classicalMusic")
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
