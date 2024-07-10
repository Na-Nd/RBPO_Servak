package ru.nand.RESTKeeperOfTheDatabase.util;

import org.aspectj.weaver.tools.ISupportsMessageContext;

public class PersonNotCreatedException extends RuntimeException{
    public PersonNotCreatedException(String message){
        super(message); // Передаём сообщение в RE
    }
}
