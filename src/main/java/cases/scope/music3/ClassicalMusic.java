package cases.scope.music3;

import framework.annotations.Component;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;
import static framework.enums.Scope.SINGLETON;


@Component("classicalMusic")
@Scope(SINGLETON)
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
