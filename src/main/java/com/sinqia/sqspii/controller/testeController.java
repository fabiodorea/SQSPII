package com.sinqia.sqspii.controller;

import com.sinqia.sqspii.data.base.entity.Teste;
import com.sinqia.sqspii.data.base.repository.TesteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teste")
public class testeController {

    @Autowired
    private TesteRepository repo;

    @GetMapping(path = "/testar")
    public String testar() {
        List<Teste> all = repo.findAll();
        return "opa";
    }

}
