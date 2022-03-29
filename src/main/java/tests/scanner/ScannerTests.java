package tests.scanner;

import framework.scanner.Scanner;

public class ScannerTests {
    public static void main(String[] args) {
        var scanner = new Scanner("tests.scanner");

        var constructor = Controller.class.getConstructors()[0];
        var parameters = constructor.getParameters();
        for (var parameter : parameters) {
            var impl = scanner.getComponent(parameter);
        }
    }
}
