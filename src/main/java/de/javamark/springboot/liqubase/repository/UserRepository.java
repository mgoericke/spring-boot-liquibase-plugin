package de.javamark.springboot.liqubase.repository;

import de.javamark.springboot.liqubase.domain.Person;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Person, Long> {
}
