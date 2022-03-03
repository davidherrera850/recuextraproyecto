package com.david.potterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.david.potterapp.data.Character;
import com.david.potterapp.data.CharacterDAO;
import com.david.potterapp.data.model.User;
import com.david.potterapp.databinding.ActivityCharacterDetailBinding;
import com.david.potterapp.ui.login.AuthenticationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class CharacterDetailHostActivity extends AppCompatActivity {

    // Hash set con los personajes sin repetir
    public static HashSet<Character> characterHashSet = new HashSet<>();

    // lista de personajes
    public static ArrayList<Character> charactersList = new ArrayList<>();

    // objeto para acceder a los personajes persistidos
    CharacterDAO characterDAO;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creamos el binding para obtener acceso a los objetos en el layout
        ActivityCharacterDetailBinding binding = ActivityCharacterDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicializamos el navigation fragment y el controler para gestionar la navegacion
        // dentro del fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_character_detail);
        NavController navController = navHostFragment.getNavController();

        // configuramos la barra de la aplicación
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // inicializamos el objeto que nos permite acceder a los datos de los personajes
        characterDAO = new CharacterDAO(this);

        // creamos la tarea asincrona para pedir a la api
        HttpResolver hr = new HttpResolver();


        try {
            // Comenzamos a pedir en segundo plano a la api por los personajes
            String results = hr.execute(Config.ENDPOINT_PERSONAJES).get();
            // Convertimos el string en un JSONArray para poder manejar los datos
            // en su naturaleza serializada y poder deserializar en objetos de nuestro
            // dominio, es decir, en personajes (Character)
            JSONArray jsonArray = new JSONArray(results);

            for (int i = 0; i < jsonArray.length(); i++) {
                // Para cada objeto de la respuesta de la api
                JSONObject explrObject = jsonArray.getJSONObject(i);
                // Extraigo sus campos: el id, el apodo, el personaje,
                // si es o no estudiante...
                int id = explrObject.getInt("id");
                String personaje = explrObject.getString("personaje");
                String apodo = explrObject.getString("apodo");
                boolean estudianteDeHogwarts = explrObject.getBoolean("estudianteDeHogwarts");
                String casaDeHogwarts = explrObject.getString("casaDeHogwarts");
                String interpretado_por = explrObject.getString("interpretado_por");
                ArrayList<String> hijos = new ArrayList<>();
                for (int j = 0; j < explrObject.getJSONArray("hijos").length(); j++) {
                    hijos.add(explrObject.getJSONArray("hijos").getString(j));
                }
                String imagen = explrObject.getString("imagen");

                // creamos un nuevo personaje
                Character newCharacter = new Character(id, personaje, apodo, estudianteDeHogwarts, casaDeHogwarts, interpretado_por, hijos, imagen);

                // guardamos el personaje
                characterDAO.save(newCharacter);
            }

            List<Character> read = new ArrayList<>();
            read = characterDAO.getCharacters();

            // metemos todos los personajes en el hash set asi evitamos duplicidades
            for (Character e : read) {
                characterHashSet.add(e);
            }

            // lo volvamos a una lista
            for (Character e : characterHashSet) {
                charactersList.add(e);
            }

            // ordenamos la lista por id de menor a mayor
            charactersList.sort(Comparator.comparing(Character::getId));


        } catch (JSONException e) {
            // Error al tratar un json
            e.printStackTrace();
        } catch (ExecutionException e) {
            // Hubo un error en la ejecución
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Se produjo una interrupción
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_character_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Callback cuando se crea el menu de opciones de arriba a la derecha
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // si se pulsa en el boton de settings
        if (id == R.id.configuration_menu){
            Intent irAPreferencias = new Intent(CharacterDetailHostActivity.this, PreferencesActivity.class);
            startActivity(irAPreferencias);
        }

        // Si se pulsa sobre el boton de suma
        if (id == R.id.add_character_menu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CharacterDetailHostActivity.this);
            LayoutInflater inflater = getLayoutInflater();

            // creamos el Alert Dialog para añadir un nuevo personaje
            builder.setView(inflater.inflate(R.layout.character_modal, null))
                    // funcion que maneja si el usuario pulsa yes
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText personaje;
                            EditText apodo;
                            Switch isStudiante;
                            EditText casa;
                            EditText interprete;
                            EditText hijos;
                            EditText imagen;
                            // recojemos los valores
                            personaje = ((AlertDialog) dialog).findViewById(R.id.personaje);
                            apodo = ((AlertDialog) dialog).findViewById(R.id.apodo);
                            isStudiante = ((AlertDialog) dialog).findViewById(R.id.isEstudiante);
                            casa = ((AlertDialog) dialog).findViewById(R.id.casa);
                            interprete = ((AlertDialog) dialog).findViewById(R.id.interprete);
                            hijos = ((AlertDialog) dialog).findViewById(R.id.hijos);
                            imagen = ((AlertDialog) dialog).findViewById(R.id.imagen);

                            // tomamos los valores que el usuario haya introducido
                            String personaje_str = personaje.getText().toString();
                            String apodo_str = apodo.getText().toString();
                            Boolean isStudiante_str = isStudiante.isActivated();
                            String casa_str = casa.getText().toString();
                            String interprete_str = interprete.getText().toString();
                            String hijos_str = hijos.getText().toString();
                            String lines[] = hijos_str.split("\\r?\\n");
                            List<String> hijos_final = Arrays.asList(lines);
                            String imagen_str = imagen.getText().toString();

                            CharacterDAO characterDAO = new CharacterDAO(CharacterDetailHostActivity.this);

                            // creamos un nuevo caracter con los valores que el usuario introdujo
                            Character nuevo = new Character(characterDAO.getLastId(), personaje_str, apodo_str, isStudiante_str, casa_str, interprete_str, hijos_final, imagen_str);
                            // guardamos el personaje
                            characterDAO.save(nuevo);
                            charactersList.add(nuevo);
                            System.out.println("La nueva lista tiene");
                            for (Character e : charactersList) {
                                System.out.println(e);
                            }

                            // establecemos una copia para actualizar la vista
                            ArrayList<Character> copy = new ArrayList<>(charactersList);
                            charactersList.clear();
                            charactersList = copy;
                            // creamos un nuevo adaptador con los datos despues de crear el personaje
                            CharacterListFragment.resetAdapterState(copy);

                            Toast.makeText(getApplicationContext(), R.string.character_added, Toast.LENGTH_SHORT).show();
                            dialog.cancel();

                        }
                    })
                    // en caso de que el usuario cancele
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.setTitle("Añadir personaje");
            builder.create();
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

}