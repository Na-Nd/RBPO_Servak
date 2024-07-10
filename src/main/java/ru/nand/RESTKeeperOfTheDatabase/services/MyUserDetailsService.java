package ru.nand.RESTKeeperOfTheDatabase.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nand.RESTKeeperOfTheDatabase.models.Person;
import ru.nand.RESTKeeperOfTheDatabase.repositories.PersonRepository;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public MyUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Вместо username будет использоваться почта, т.к. она уникальная у каждого пользователя
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // Будем передавать сюда почту, а не имя
        Person person = personRepository.findByEmail(username);
        if(person == null){
            throw new UsernameNotFoundException("User not found with email: "+ username);
        }
        return new User(person.getEmail(), person.getPassword(), new ArrayList<>());
    }
}
