package ru.nand.rbpo2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.nand.rbpo2.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
