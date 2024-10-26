package ru.mtuci.rbpo2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo2.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
