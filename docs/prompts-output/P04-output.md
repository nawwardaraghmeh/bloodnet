## P04 — JPA entities → DAO layer

## BaseDao

```java
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
```

## DAOs

```java
// DonorDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonorDao extends BaseDao<Donor> {

    public DonorDao() {
        super(Donor.class);
    }
}
```

```java
// DonationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonationDao extends BaseDao<Donation> {

    public DonationDao() {
        super(Donation.class);
    }
}
```

```java
// BloodPackDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BloodPackDao extends BaseDao<BloodPack> {

    public BloodPackDao() {
        super(BloodPack.class);
    }
}
```

```java
// StorageLocationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageLocationDao extends BaseDao<StorageLocation> {

    public StorageLocationDao() {
        super(StorageLocation.class);
    }
}
```

```java
// PatientDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientDao extends BaseDao<Patient> {

    public PatientDao() {
        super(Patient.class);
    }
}
```

```java
// IssueDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IssueDao extends BaseDao<Issue> {

    public IssueDao() {
        super(Issue.class);
    }
}
```