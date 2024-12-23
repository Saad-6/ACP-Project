package acp.acp_project.Repository;

import java.util.List;

public interface IGenericRepository<T> {
    T create(T entity);

    List<T> getAll();

    T getById(int id);

    T update(T entity);

    void delete(int id);
}
