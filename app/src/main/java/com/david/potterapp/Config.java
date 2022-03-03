package com.david.potterapp;

public class Config {
    // Clase que contiene parametros de configuración

    // url de la api
    public static final String URL="https://fedeperin-harry-potter-api.herokuapp.com";

    // endpoint de los personajes
    public static final String ENDPOINT_PERSONAJES = URL +"/" + "personajes";

    // maximo timout para una conexion http
    public static final int TIMEOUT_CONNECTION = 20000;

    // maximo timeout entre paquetes http
    public static final int TIMEOUT_READ = 20000;
}
