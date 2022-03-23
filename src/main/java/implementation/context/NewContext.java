package implementation.context;

import cases.app.controllers.MailController;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NewContext {

    private final String valuesToScan;

    private static NewContext context;

    private NewContext(@Nullable String valuesToScan) {
        this.valuesToScan = valuesToScan;

    }

    public static void Start(Class<?> mainClass) {
        Start(mainClass, null);
    }

    public static void Start(Class<?> mainClass, @Nullable String valuesToScan) {
        if (context == null) {
            context = new NewContext(valuesToScan);
            context.Run(mainClass);
        }
    }

    public static <T> T getType(Class<T> clazz) {
        return null;
    }

    @SneakyThrows
    private void Run(Class<?> mainClass) {
        var module = mainClass.getModule();
        var packs = module.getPackages();
        var packageToScan = mainClass.getPackageName();

        var scanner = new Reflections(packageToScan, new SubTypesScanner(false));
        var types = scanner.getAllTypes().stream().collect(Collectors.toList());
        var classes = getClasses(types, module);
    }

    private List<Class<?>> getClasses(List<String> names, Module module) {
        return names.stream().map(n -> Class.forName(module, n)).collect(Collectors.toList());
    }
}
