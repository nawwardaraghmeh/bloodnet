package it.unifi.swam.dao;

// BaseDao.java
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

public abstract class BaseDao<T> {

    @Inject
    protected EntityManager em;

    private final Class<T> entityClass;

    protected BaseDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T find(Long id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }

    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    public T update(T entity) {
        return em.merge(entity);
    }

    public void delete(T entity) {
        T managed = em.contains(entity) ? entity : em.merge(entity);
        em.remove(managed);
    }
}