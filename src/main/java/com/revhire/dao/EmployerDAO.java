package com.revhire.dao;

import com.revhire.model.Employer;
import java.util.Optional;

public interface EmployerDAO {
    void create(Employer employer);

    Optional<Employer> findByUserId(int userId);

    boolean update(Employer employer);
}
