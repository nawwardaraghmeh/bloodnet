package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

// IssueDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IssueDao extends BaseDao<Issue> {

    public IssueDao() {
        super(Issue.class);
    }
}