package cases.scope.music2;

import framework.annotations.Component;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;


@Component("classicalMusic")
@Scope(PROTOTYPE)
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
