package com.blogs.learningblog.repos;

import com.blogs.learningblog.models.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {
}
