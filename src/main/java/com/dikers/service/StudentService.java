package com.dikers.service;

import com.dikers.annotation.Autowired;
import com.dikers.annotation.Component;
import com.dikers.pojo.Student;


@Component
public class StudentService {

    @Autowired
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }


    public void print(Student student){
        System.out.println("student: "+ student);
    }

}