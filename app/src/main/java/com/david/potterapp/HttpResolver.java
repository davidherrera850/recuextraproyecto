package com.david.potterapp;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Clase que modela nuestra tarea asincrona para hacer la peticion a la api de harry potter.
public class HttpResolver extends AsyncTask<String, Void, String> {


    // Esta tarea se hará en una hebra adicional de forma concurrente y en segundo plano.
    @Override
    protected String doInBackground(String... params) {
        try {
            // Creamos un objeto url a partir del string que le pasamos por parametro
            URL url = new URL(params[0]);
            HttpURLConnection connection;
            try {
                // Creamos una conexión http
                connection = (HttpURLConnection) url.openConnection();
                // para intercambiar mensajes get
                connection.setRequestMethod("GET");
                // desactivamos la cache en la peticion
                connection.setUseCaches(false);
                // desactivamos la posibilida de interaccion con el usuario en la conexion
                connection.setAllowUserInteraction(false);
                // establecemos el timout en la conexion
                connection.setConnectTimeout(Config.TIMEOUT_CONNECTION);
                // establecemos el timeout de lectura de paquetes http
                connection.setReadTimeout(Config.TIMEOUT_READ);
                int status = connection.getResponseCode();

                // tras leer el codigo de respuesta que nos devuelve la api
                // si devuelve un 201, comenzamos a leer el cuerpo.
                switch (status) {
                    case 200:
                    case 201:
                        // si la api responde con un http status de 201
                        // significa que nos devuelve datos porque la conexion es correcta
                        // Abrimos un lector con buffer a partir del flujo de entrada de la conexion
                        // esto nos permite obtener sobre el objeto burffered reader
                        // todo el cuerpo de la respuesta que nos devuelve la api
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        // ahora transformamos esa respuesta en bruto a un string
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        // cuando tenemos el string leido, cerramos la conexion
                        br.close();
                        return sb.toString();
                }
            } catch (IOException e) {
                // hubo un error de entrada y salida
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // la url que le pasamos no es correcta o está mal formada
            e.printStackTrace();
        }

        return null;
    }

}
