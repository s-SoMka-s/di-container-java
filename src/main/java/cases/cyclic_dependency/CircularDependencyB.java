package cases.cyclic_dependency;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class CircularDependencyB {

    private CircularDependencyA circA;

    @Autowired
    public CircularDependencyB(CircularDependencyA circA) {
        this.circA = circA;
    }
}