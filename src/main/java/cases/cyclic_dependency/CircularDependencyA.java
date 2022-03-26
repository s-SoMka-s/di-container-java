package cases.cyclic_dependency;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public CircularDependencyA(CircularDependencyB circB) {
        this.circB = circB;
    }
}