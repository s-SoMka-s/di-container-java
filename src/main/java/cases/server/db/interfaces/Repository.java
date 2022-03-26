package cases.server.db.interfaces;

import java.util.List;

public interface Repository<T> {
    List<T> getAll();

    void add(T entity);
}
