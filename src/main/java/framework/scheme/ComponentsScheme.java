package framework.scheme;

import framework.components.ComponentClass;
import framework.components.ComponentNode;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentsScheme {
    private final ArrayList<ComponentNode> nodes;

    public ComponentsScheme(ArrayList<ComponentNode> nodes) {
        this.nodes = nodes;
    }

    public void ensureHasNoCircularDependency() {
        if (nodes.isEmpty()) {
            return;
        }

        var colors = new HashMap<ComponentClass, Colors>();
        for (var node : nodes) {
            colors.put(node.getComponent(), Colors.White);
        }

        var start = nodes.get(0).getComponent();
        var pair = new Pair();
        var ancestors = new HashMap<ComponentClass, ComponentClass>();
        var res = hasCycle(start, colors, ancestors, pair);
        if (res) {
            printCycle(ancestors, pair);
        }
    }

    private void printCycle(HashMap<ComponentClass, ComponentClass> ancestors, Pair cyclicPair) {
        var cycle = new ArrayList<ComponentClass>();
        cycle.add(cyclicPair.cycleStart);
        for (var v = cyclicPair.cycleEnd; v != cyclicPair.cycleStart; v = ancestors.get(v)) {
            cycle.add(v);
        }

        Collections.reverse(cycle);

        for (var item : cycle) {
            System.out.println(item.getName());
        }
    }

    private boolean hasCycle(ComponentClass current, HashMap<ComponentClass, Colors> colors, HashMap<ComponentClass, ComponentClass> ancestors, Pair cyclicPair) {
        colors.put(current, Colors.Grey);

        for (var childNode : current.getChildNodes())
        {
            var childComponent = childNode.getComponent();
            if (colors.get(childComponent) == Colors.Grey)
            {
                System.out.println("Circular dependency");
                cyclicPair.setCycleStart(childComponent);
                cyclicPair.setCycleEnd(current);
                return true;
            }
            if (colors.get(childComponent) == Colors.White) {
                ancestors.put(childComponent, current);
                if (hasCycle(childComponent, colors, ancestors, cyclicPair)){
                    return true;
                }
            }
        }
        colors.put(current, Colors.Black);
        return false;
    }

    public Set<ComponentClass> getRootComponents() {
        return nodes.stream().map(n -> n.getComponent()).collect(Collectors.toSet());
    }

    private enum Colors {
        White,
        Grey,
        Black
    }

    private class Pair {
        private ComponentClass cycleStart;
        private  ComponentClass cycleEnd;

        public void setCycleStart(ComponentClass start) {
            this.cycleStart = start;
        }

        public void setCycleEnd(ComponentClass end) {
            this.cycleEnd = end;
        }
    }
}
