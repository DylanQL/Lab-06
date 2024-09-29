package com.example.demo.controllers;

import com.example.demo.domain.entities.Alumno;
import com.example.demo.services.AlumnoService; // Asegúrate de tener este servicio implementado
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;

////////////////////


@Controller
@SessionAttributes("alumno")
public class AlumnoController {

    @Autowired
    AlumnoService alumnoService;

    @RequestMapping(value = "/alumnos", method = RequestMethod.GET)
    public String listarAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        return "listarAlumnos";
    }

    @RequestMapping(value = "/formAlumno")
    public String crearAlumno(Map<String, Object> model) {
        Alumno alumno = new Alumno();
        model.put("alumno", alumno);
        return "formAlumno";
    }

    @RequestMapping(value = "/formAlumno", method = RequestMethod.POST)
    public String guardarAlumno(@Valid Alumno alumno, BindingResult result, Model model, SessionStatus status) {
        if (result.hasErrors()) {
            return "formAlumno";
        }
        alumnoService.grabar(alumno);
        status.setComplete();
        return "redirect:/"; // Redirige a la URL raíz
    }

    @RequestMapping(value = "/formAlumno/{id}")
    public String editarAlumno(@PathVariable(value = "id") Integer id, Map<String, Object> model) {
        Alumno alumno = null;
        if (id > 0) {
            alumno = alumnoService.buscar(id);
        } else {
            return "redirect:/alumnos";
        }
        model.put("alumno", alumno);
        return "formAlumno";
    }

    @RequestMapping(value = "/eliminarAlumno/{id}")
    public String eliminarAlumno(@PathVariable(value = "id") Integer id) {
        if (id > 0) {
            alumnoService.eliminar(id);
        }
        return "redirect:/alumnos";
    }

    @RequestMapping(value = "/verAlumnos", method = RequestMethod.GET)
    public String verAlumnos(Model model, @RequestParam(required = false) String format) {
        model.addAttribute("alumnos", alumnoService.listar());
        model.addAttribute("titulo", "Lista de alumnos");

        if ("pdf".equals(format)) {
            return "alumno/ver.pdf";
        } else if ("xlsx".equals(format)) {
            return "alumno/ver.xlsx";
        }

        return "alumno/ver";
    }
}