package ru.nand.RESTKeeperOfTheDatabase.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.nand.RESTKeeperOfTheDatabase.dto.PersonDTO;
import ru.nand.RESTKeeperOfTheDatabase.models.Person;
import ru.nand.RESTKeeperOfTheDatabase.services.MyUserDetailsService;
import ru.nand.RESTKeeperOfTheDatabase.services.PersonService;
import ru.nand.RESTKeeperOfTheDatabase.util.JwtUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    private MockMvc mockMvc;

    @Autowired
    public PersonControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockBean
    private PersonService personService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }

    @Test
    public void testRegister() throws Exception{
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName("ivan");
        personDTO.setAge(19);
        personDTO.setEmail("ivan@mail.com");
        personDTO.setPassword("encodedPassword");

        Person person = new Person("ivan", 19, "ivan@mail.com", "encodedPassword");

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(modelMapper.map(any(PersonDTO.class), any(Class.class))).thenReturn("person");
        when(jwtUtil.generateToken(any())).thenReturn("dummyToken");

        mockMvc.perform(post("/person/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ivan\",\"age\":19,\"email\":\"ivan@mail.com\",\"password\":\"qwerty\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"jwt\":\"dummyToken\"}"));

    }
}
