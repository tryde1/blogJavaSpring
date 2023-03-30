package com.blogs.learningblog.repos;

import com.blogs.learningblog.models.Article;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {
}
