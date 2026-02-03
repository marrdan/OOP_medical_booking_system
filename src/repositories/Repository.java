package repositories;

import java.util.List;

public interface Repository<T> {
    void save(T entity);
    void delete(int id);
    T findById(int id);
    List<T> findAll();

}
