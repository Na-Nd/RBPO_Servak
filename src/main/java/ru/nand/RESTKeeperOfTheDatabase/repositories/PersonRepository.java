package ru.nand.RESTKeeperOfTheDatabase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nand.RESTKeeperOfTheDatabase.models.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

}
