package com.david.potterapp.data;

import java.util.ArrayList;
import java.util.List;

// Modela nuestra forma de representar un personaje
public class Character implements Comparable<Character> {
    private int id;
    private String personaje;
    private String apodo;
    private boolean esEstudianteDeHogwarts;
    private String casaDeHogwarts;
    private String interprete;
    private List<String> hijos;
    private String imagen;

    public Character() {
    }

    public Character(int id, String personaje, String apodo, boolean esEstudianteDeHogwarts, String casaDeHogwarts, String interprete, List<String> hijos, String imagen) {
        this.id = id;
        this.personaje = personaje != null? personaje: "";
        this.apodo = apodo != null? apodo: "";
        this.esEstudianteDeHogwarts = esEstudianteDeHogwarts;
        this.casaDeHogwarts = casaDeHogwarts != null? casaDeHogwarts: "";
        this.interprete = interprete != null? interprete: "";
        this.hijos = hijos != null? hijos: new ArrayList<>();
        this.imagen = imagen != null? imagen: "";
    }

    public Character(String personaje_str, String apodo_str, Boolean isStudiante_str, String casa_str, String interprete_str, List<String> hijos_final, String imagen_str) {
        this.id = -1;
        this.personaje = personaje != null? personaje: "";
        this.apodo = apodo != null? apodo: "";
        this.esEstudianteDeHogwarts = esEstudianteDeHogwarts;
        this.casaDeHogwarts = casaDeHogwarts != null? casaDeHogwarts: "";
        this.interprete = interprete != null? interprete: "";
        this.hijos = hijos != null? hijos: new ArrayList<>();
        this.imagen = imagen != null? imagen: "";
    }

    public  int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonaje() {
        return personaje;
    }

    public void setPersonaje(String personaje) {
        this.personaje = personaje;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public boolean isEsEstudianteDeHogwarts() {
        return esEstudianteDeHogwarts;
    }

    public void setEsEstudianteDeHogwarts(boolean esEstudianteDeHogwarts) {
        this.esEstudianteDeHogwarts = esEstudianteDeHogwarts;
    }

    public String getCasaDeHogwarts() {
        return casaDeHogwarts;
    }

    public void setCasaDeHogwarts(String casaDeHogwarts) {
        this.casaDeHogwarts = casaDeHogwarts;
    }

    public String getInterprete() {
        return interprete;
    }

    public void setInterprete(String interprete) {
        this.interprete = interprete;
    }

    public List<String> getHijos() {
        return hijos;
    }

    public void setHijos(List<String> hijos) {
        this.hijos = hijos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", personaje='" + personaje + '\'' +
                ", apodo='" + apodo + '\'' +
                ", esEstudianteDeHogwarts=" + esEstudianteDeHogwarts +
                ", casaDeHogwarts='" + casaDeHogwarts + '\'' +
                ", interprete='" + interprete + '\'' +
                ", hijos=" + hijos +
                ", imagen='" + imagen + '\'' +
                '}';
    }

    // con este metodo podemos establecer relacion de orden entre dos peronajes
    // un caracter es menor que otro si su id es menor
    @Override
    public int compareTo(Character s) {
        return id < s.getId() ? 1:0 ;
    }

    // con este metodo podemos establecer el criterio de cuando dos personajes son iguales
    // en este caso es por el personaje
    public boolean equals(Character o) {
        if (o == this)
            return true;
        return this.personaje == o.getPersonaje();
    }



}
