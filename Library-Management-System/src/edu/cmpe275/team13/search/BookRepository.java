package edu.cmpe275.team13.search;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

public interface BookRepository<Book> extends Repository<Book, Long>, JpaSpecificationExecutor<Book> {
}
