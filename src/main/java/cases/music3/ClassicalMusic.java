package cases.music3;

import org.springframework.stereotype.Component;

import javax.inject.Named;

//@Named
@Component
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "In the Hall of the Mountain King ";
    }
}
