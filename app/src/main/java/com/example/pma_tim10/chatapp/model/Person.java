package com.example.pma_tim10.chatapp.model;

/**
 * Created by Dorian on 4/25/2017.
 */

//Basic model, only for testing purpose
public class Person {

    private String name;
    private String surname;
    private String image;
    private Integer age;

    public Person() {

    }

    public Person(String name, String surname, String image, Integer age) {
        this.name = name;
        this.surname = surname;
        this.image = image;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
