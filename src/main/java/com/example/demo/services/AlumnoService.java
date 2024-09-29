package com.example.demo.services;

import com.example.demo.domain.entities.Alumno; // Asegúrate de tener la entidad Alumno

import java.util.List;

public interface AlumnoService {
    void grabar(Alumno alumno);
    void eliminar(int id);
    Alumno buscar(Integer id);
    List<Alumno> listar();
}