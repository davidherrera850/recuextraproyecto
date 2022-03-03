package com.david.potterapp;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.DragEvent;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.david.potterapp.data.Character;
import com.david.potterapp.data.CharacterDAO;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.david.potterapp.placeholder.PlaceholderContent;
import com.david.potterapp.databinding.FragmentCharacterDetailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a single Character detail screen.
 * This fragment is either contained in a {@link CharacterListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class CharacterDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private static FloatingActionButton edit;
    /**
     * The placeholder content this fragment is presenting.
     */
    private PlaceholderContent.PlaceholderItem mItem;
    private ActionBar actionBar;
    private TextView personaje;
    private TextView apodo;
    private TextView estudiante;
    private TextView casa;
    private TextView interprete;
    private TextView hijos;
    private ImageView image;

    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);

            mItem = PlaceholderContent.ITEM_MAP.get(Integer.parseInt(clipDataItem.getText().toString()));
            updateContent();
        }
        return true;
    };
    private FragmentCharacterDetailBinding binding;

    // Contructor obligatorio del fragmento
    public CharacterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            mItem = PlaceholderContent.ITEM_MAP.get(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // al crear la vista del fragment incializamos el binding para acceder
        // a los datos del xml
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // inicializamos la action bar
        actionBar = ((CharacterDetailHostActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // inicializamos cada uno de los componentes del layout xml
        personaje = binding.characterDetail;
        apodo = binding.characterApodoTv;

        casa = binding.characterCasa;
        interprete = binding.characterInterprete;
        estudiante = binding.characterEstudianteTv;
        hijos = binding.characterHijos;

        edit = binding.editFab;

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Vas a editar");
                // Mostrar un builder con los datos rellenos
                // guardar tal y como venga en la base de datos
                // recargar el adapter

                // tengo los datos en mItem
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogo = inflater.inflate(R.layout.character_modal, null);

                EditText personaje = dialogo.findViewById(R.id.personaje);
                personaje.setText(mItem.character);
                EditText apodo = dialogo.findViewById(R.id.apodo);
                apodo.setText(mItem.apodo);
                Switch isStudiante = dialogo.findViewById(R.id.isEstudiante);
                isStudiante.setChecked(mItem.isHogwartsStudent);
                EditText casa = dialogo.findViewById(R.id.casa);
                casa.setText(mItem.HogwartsHome);
                EditText interprete = dialogo.findViewById(R.id.interprete);
                interprete.setText(mItem.actor);
                EditText hijos = dialogo.findViewById(R.id.hijos);
                hijos.setText(mItem.childs.toString().toString());
                EditText imagen = dialogo.findViewById(R.id.imagen);
                imagen.setText(mItem.image);


                builder.setView(dialogo)
                        // si pulsa en que si
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

                                // inicializamos las variables editText y Switch del alert
                                personaje = ((AlertDialog) dialog).findViewById(R.id.personaje);
                                apodo = ((AlertDialog) dialog).findViewById(R.id.apodo);
                                isStudiante = ((AlertDialog) dialog).findViewById(R.id.isEstudiante);
                                casa = ((AlertDialog) dialog).findViewById(R.id.casa);
                                interprete = ((AlertDialog) dialog).findViewById(R.id.interprete);
                                hijos = ((AlertDialog) dialog).findViewById(R.id.hijos);
                                imagen = ((AlertDialog) dialog).findViewById(R.id.imagen);

                                // recogemos valores que haya introducido el usuairo
                                String personaje_str = personaje.getText().toString();
                                String apodo_str = apodo.getText().toString();
                                Boolean isStudiante_str = isStudiante.isActivated();
                                String casa_str = casa.getText().toString();
                                String interprete_str = interprete.getText().toString();
                                String hijos_str = hijos.getText().toString();
                                String lines[] = hijos_str.split("\\r?\\n");
                                List<String> hijos_final = Arrays.asList(lines);
                                String imagen_str = imagen.getText().toString();

                                CharacterDAO characterDAO = new CharacterDAO(getContext());
                                // guardamos el personaje
                                Character nuevo = new Character(characterDAO.getLastId(), personaje_str, apodo_str, isStudiante_str, casa_str, interprete_str, hijos_final, imagen_str);

                                for(Character e: CharacterDetailHostActivity.charactersList){
                                    // Si alguno de la lista es el seleccionado
                                    if (e.getId() == mItem.id){
                                        e.setId(mItem.id);

                                        // guardamos
                                        e.setPersonaje(nuevo.getPersonaje());
                                        e.setApodo(nuevo.getApodo());
                                        e.setEsEstudianteDeHogwarts(nuevo.isEsEstudianteDeHogwarts());
                                        e.setCasaDeHogwarts(nuevo.getCasaDeHogwarts());
                                        e.setInterprete(nuevo.getInterprete());
                                        e.setHijos(nuevo.getHijos());
                                        e.setImagen(nuevo.getImagen());

                                        // cambiamos el valor del detalle del item actual
                                        mItem.apodo = nuevo.getApodo();
                                        mItem.character = nuevo.getPersonaje();
                                        mItem.isHogwartsStudent = nuevo.isEsEstudianteDeHogwarts();
                                        mItem.HogwartsHome = nuevo.getCasaDeHogwarts();
                                        mItem.actor = nuevo.getInterprete();
                                        mItem.childs = nuevo.getHijos();
                                        mItem.image = nuevo.getImagen();
                                        characterDAO.deleteCharacter(e.getPersonaje());
                                        characterDAO.save(e);
                                    }
                                }

                                System.out.println("La nueva lista editada tiene");
                                for (Character e : CharacterDetailHostActivity.charactersList) {
                                    System.out.println(e);
                                }
                                 //establecemos copia y actualizamos el adaptador
                                ArrayList<Character> copy = new ArrayList<>(CharacterDetailHostActivity.charactersList);
                                CharacterDetailHostActivity.charactersList.clear();
                                CharacterDetailHostActivity.charactersList = copy;
                                CharacterListFragment.resetAdapterState(copy);

                                Toast.makeText(getContext(), R.string.character_added, Toast.LENGTH_SHORT).show();
                                dialog.cancel();

                                updateContent();
                            }
                        })
                        // si el usuario indica que no
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setTitle("Editar personaje personaje");
                builder.create();
                builder.show();

            }
        });
        // actualizamos el contenido
        updateContent();
        //cargamos la imagen
        loadImage();
        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContent() {
        // siempre que no sea nulo, establecemos valor al detalle
        if (mItem != null) {

            personaje.setText("Personaje: " + mItem.character);
            apodo.setText("Apodo: " + mItem.apodo);
            estudiante.setText("Estudiante: " + String.valueOf(mItem.isHogwartsStudent));

            casa.setText("Casa: " + mItem.HogwartsHome);
            interprete.setText("Interprete: " + mItem.actor);

            String hijosStr = "";
            for (String i : mItem.childs) {
                hijosStr += "- " + i + "\n";
            }

            hijos.setText("Hijos: \n" + hijosStr);

            if (actionBar != null) {
                actionBar.setTitle(mItem.apodo);
            }
        }
    }

    private void loadImage() {
        // si el item no es nulo cargamos la imagen con glide
        if (mItem != null) {
            Glide.with(this)
                    .load(mItem.image)
                    // la cortamos por el centro
                    .fitCenter()
                    // el resulatado lo intodcimos el el image view
                    .into(binding.characterImage);
        }

    }
}