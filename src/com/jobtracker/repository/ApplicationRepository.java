package com.jobtracker.repository;

import com.jobtracker.model.Application;
import java.util.List;

public interface ApplicationRepository {

    List<Application> findAll();

    Application findById(int id);

    void save(Application application);

    int nextId();


}
