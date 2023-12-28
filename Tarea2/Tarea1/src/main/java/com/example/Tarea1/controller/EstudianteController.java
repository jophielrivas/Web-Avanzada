package com.example.Tarea1.controller;

import com.example.Tarea1.model.Estudiante;
import com.example.Tarea1.repo.EstudianteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EstudianteController {
    @Autowired
    private EstudianteRepo repo;

    @GetMapping({ "/estudiantes", "", "/"})
    public String listarEstudiantes(Model modelo) {
        modelo.addAttribute("estudiantes", repo.findAll());
        return "listEstudiantes"; // nos retorna al archivo estudiantes
    }

    @GetMapping({"/estudiantes/new"})
    public String nuevoEstudiante(Model modelo) {
        modelo.addAttribute("estudiante", new Estudiante());
        modelo.addAttribute("titulo", "Nuevo Estudiante");
        return "newEstudiante";
    }

    @PostMapping("/estudiantes/save")
    public String saveEstudiante(Estudiante estudiante, RedirectAttributes ra) {
        System.out.println(estudiante.getId());
        repo.save(estudiante);
        ra.addFlashAttribute("message", "El estudiante se ha agregado correctamente.");
        return "redirect:/estudiantes";
    }

    @GetMapping({"/estudiantes/ver/{id}"})
    public String verEstudiante(@PathVariable("id") Long id, Model modelo) {
        Estudiante estudiante = repo.getReferenceById(id);
        modelo.addAttribute("estudiante", estudiante);
        modelo.addAttribute("titulo", "Ver Estudiante (ID:" + id + ")");
        return "verEstudiante";
    }

    @GetMapping({"/estudiantes/edit/{id}"})
    public String editEstudiante(@PathVariable("id") Long id, Model modelo) {
        Estudiante estudiante = repo.getReferenceById(id);
        modelo.addAttribute("estudiante", estudiante);
        modelo.addAttribute("titulo", "Editar Estudiante (ID:" + id + ")");
        modelo.addAttribute("check",'V');
        return "newEstudiante";
    }

    @GetMapping({"/estudiantes/delete/{id}"})
    public String deleteEstudiante(@PathVariable("id") Long id,RedirectAttributes ra) {
        repo.delete(repo.getReferenceById(id));
        ra.addFlashAttribute("message", "The user ID " + id + " has been deleted.");
        return "redirect:/estudiantes";
    }



}


