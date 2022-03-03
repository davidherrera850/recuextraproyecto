package com.david.potterapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.david.potterapp.data.Character;
import com.david.potterapp.data.CharacterDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;


import com.david.potterapp.databinding.ActivityPreferencesBinding;

public class PreferencesActivity extends AppCompatActivity {

    ActionBar actionBar;
    Button resetPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el binding en vez de findViewById pero seria igual de valido usar findViewById.
        // Esto es más rápido y cómodo.
        ActivityPreferencesBinding binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicializamos el boton de reset
        resetPreferences = binding.resetBdBtn;

        // establecemos el titulo de la actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_preferences);


        // asociamos al boton de reset un objeto listener, que implementa el comportamiento de onClik
        resetPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // en cuanto haga click, inicializamos el DAO = Data Access Object sobre los personajes
                CharacterDAO characterDAO = new CharacterDAO(PreferencesActivity.this);
                // nos traemos en una lista los personajes que existieran en la BD
                ArrayList<Character> characterArrayList = characterDAO.getCharacters();



                // Borramos la lista de caracteres que existieran
                for(Character c: characterArrayList){
                    characterDAO.deleteCharacter(c.getPersonaje());
                }

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

                        // Con los datos en crudo extraidos, creo un objeto Character
                        Character newCharacter = new Character(id, personaje, apodo, estudianteDeHogwarts, casaDeHogwarts, interpretado_por, hijos, imagen);
                        // le indico al dao que lo guarde ese objeto y lo persista.
                        characterDAO.save(newCharacter);
                    }


                    // Imprimo los personajes que he obtenido
                    ArrayList<Character> charactersList = characterDAO.getCharacters();
                    for (Character e : charactersList) {
                        System.out.println(e);
                    }
                    // Realizo una copia de la lista, y se la paso a CharacterListFragment,
                    // para que actualize la lista maestro detalle.
                    ArrayList<Character> copy = new ArrayList<>(charactersList);
                    charactersList.clear();
                    charactersList = copy;
                    CharacterListFragment.resetAdapterState(copy);

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
        });

    }
    @Override
    public void onBackPressed()
    {
        // Sobreescribimos este callback, el de pulsar el boton hacia atrás
        // para volver de nuevo a la actividad de lista maestro detalle de los personajes.
        // no usamos finish para forzar a iniciar de nuevo desde cero la actividad y leer desde la
        // base de datos
        Intent goToCharacterDetailHostActivity = new Intent(this,CharacterDetailHostActivity.class);
        startActivity(goToCharacterDetailHostActivity);
    }
}