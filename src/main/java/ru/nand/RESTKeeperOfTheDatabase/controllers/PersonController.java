package ru.nand.RESTKeeperOfTheDatabase.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.nand.RESTKeeperOfTheDatabase.dto.PersonDTO;
import ru.nand.RESTKeeperOfTheDatabase.models.Person;
import ru.nand.RESTKeeperOfTheDatabase.services.MyUserDetailsService;
import ru.nand.RESTKeeperOfTheDatabase.services.PersonService;
import ru.nand.RESTKeeperOfTheDatabase.util.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public PersonController(PersonService personService, ModelMapper modelMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, MyUserDetailsService myUserDetailsService) {
        this.personService = personService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.myUserDetailsService = myUserDetailsService;
    }

    @GetMapping()
    public List<PersonDTO> getPeople(){
        return personService.findAll().stream().map(this::convertToPersonDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getCurrentPerson(@PathVariable("id") int id){
        return convertToPersonDTO(personService.findOne(id));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors){
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
            }

            throw new PersonNotCreatedException(errorMessage.toString());
        }

        personDTO.setPassword(passwordEncoder.encode(personDTO.getPassword()));
        Person person = convertToPerson(personDTO);
        personService.save(person);

        String jwt = jwtUtil.generateToken(myUserDetailsService.loadUserByUsername(person.getEmail()));
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse("Person with this id was not found", System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    public PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }

    public Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);
    }
}
