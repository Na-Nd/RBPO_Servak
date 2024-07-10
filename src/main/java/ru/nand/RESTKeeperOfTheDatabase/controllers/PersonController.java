package ru.nand.RESTKeeperOfTheDatabase.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.nand.RESTKeeperOfTheDatabase.dto.PersonDTO;
import ru.nand.RESTKeeperOfTheDatabase.models.Person;
import ru.nand.RESTKeeperOfTheDatabase.services.PersonService;
import ru.nand.RESTKeeperOfTheDatabase.util.PersonErrorResponse;
import ru.nand.RESTKeeperOfTheDatabase.util.PersonNotCreatedException;
import ru.nand.RESTKeeperOfTheDatabase.util.PersonNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;
    private final ModelMapper modelMapper;


    @Autowired
    public PersonController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getPeople(){
        return personService.findAll().stream().map(this::convertToPersonDTO).collect(Collectors.toList()); // Jackson конвертирует их в JSON
    }

    @GetMapping("/{id}")
    public PersonDTO getCurrentPerson(@PathVariable("id") int id){
        return convertToPersonDTO(personService.findOne(id));
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage =new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors){
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
            }

            throw new PersonNotCreatedException(errorMessage.toString());
        }

        personService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK); // Возвращаем ответ 200
    }

    @ExceptionHandler // Ловим PersonNotFound и возвращаем JSON
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse("Person with this id was not found", System.currentTimeMillis());

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Перегрузка
    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(e.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    public PersonDTO convertToPersonDTO(Person person){ // Person -> PersonDTO
        return modelMapper.map(person, PersonDTO.class);
    }

    public Person convertToPerson(PersonDTO personDTO){ // PersonDTO -> Person
        return modelMapper.map(personDTO, Person.class);
    }


}
