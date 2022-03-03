package com.david.potterapp;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.david.potterapp.data.Character;
import com.david.potterapp.data.CharacterDAO;
import com.david.potterapp.databinding.FragmentCharacterListBinding;
import com.david.potterapp.databinding.CharacterListContentBinding;

import com.david.potterapp.placeholder.PlaceholderContent;

import java.util.List;

/**
 * A fragment representing a list of Characters. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link CharacterDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CharacterListFragment extends Fragment {

    // Adaptador
    static SimpleItemRecyclerViewAdapter adapter;

    // Recycler View
    static RecyclerView recyclerView;

    // Objeto vista con el detalle de un item
    static View itemDetailFragmentContainer;

    // Objeto que vincula la vista de la lista
    private FragmentCharacterListBinding binding;


    // crea un nuevo adaptador con la nueva lista de personajes
    // esto sirve para recargar la lista original en caso de cambios
    public static void resetAdapterState(List<Character> l){
        PlaceholderContent.ITEMS.clear();
        PlaceholderContent.ITEM_MAP.clear();

        for(Character e: l){
            // Creamos un objeto placeholder, que es un item de la lista
            PlaceholderContent.addItem(new PlaceholderContent.PlaceholderItem(e.getId(),
                    e.getPersonaje(),
                    e.getApodo(),
                    e.isEsEstudianteDeHogwarts(),
                    e.getCasaDeHogwarts(),
                    e.getInterprete(),
                    e.getHijos(),
                    e.getImagen()
                    )
            );
        }
        //creamos un adaptador nuevo con los items y la vista del detalle
        adapter = new SimpleItemRecyclerViewAdapter(
                PlaceholderContent.ITEMS,
                itemDetailFragmentContainer
        );

        // vinculamos al recyclerview el adaptador.
        recyclerView.setAdapter(adapter);
    }



    // Cuando se crea la vista del fragment de la lista
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflamos la vista del fragmento
        binding = FragmentCharacterListBinding.inflate(inflater, container, false);
        // Establecemos el titulo de la action Bar
        ((CharacterDetailHostActivity) requireActivity()).getSupportActionBar().setTitle(R.string.list_title);
        return binding.getRoot();

    }


    // Cuando la vista del fragment ya está creada
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // inicializamos la lista de personajes, es decir, el array list
        recyclerView = binding.characterList;


        // Creamos un ItemTouchHelper que nos sirve para establecer un callback cuando haya un click
        // en este caso un drag, desliz sobre un elemento, le indicamos que la dirección valida es
        // o de derecha a izquierda o de izquierda a derecha, cualquiera de los dos vale
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // Cuando detecte movimiento. No hacemos nada, pero estamos obligados a implementar este metodo
            // lo dejamos por defecto
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int position = viewHolder.getAdapterPosition();
                return false;
            }

            // Este es el callback, la funcion que va a responder cuando se haya completado el gesto swipe
            // es decir, el desliz de un elemento de la lista
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                // Obtenemos el elemento a borrar
                PlaceholderContent.PlaceholderItem toDelete = adapter.mValues.get(position);
                // Informamos al usuario con un toas de que se ha eliminado este elemento
                Toast.makeText(getActivity(), "Eliminado "+ adapter.mValues.get(position).apodo, Toast.LENGTH_SHORT).show();

                // eliminamos del adaptador para que no se vea en la lista
                adapter.mValues.remove(position);
                adapter.notifyDataSetChanged();
                CharacterDAO characterDAO = new CharacterDAO(getContext());
                // Lo eliminamos de la base de datos tambien
                characterDAO.deleteCharacter(toDelete.character);

            }
        };
        // Creamos el ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        // Vinculamos el itemtouchhelper al recycler view que es el que contiene los elementos
        //  de la lista
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // creamos el detalle del item pulsado
        itemDetailFragmentContainer = view.findViewById(R.id.character_detail_nav_container);

        // inicializamos el recyclerview
        setupRecyclerView(recyclerView, itemDetailFragmentContainer);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View itemDetailFragmentContainer
    ) {
        // Creamos un adaptador con los elementos y la vista a rellenar
        adapter = new SimpleItemRecyclerViewAdapter(
                PlaceholderContent.ITEMS,
                itemDetailFragmentContainer
        );

        // vinculamos al recycler view el adaptador recien creado
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        // cuando se destruya la vista desvinculamos todas las referencias a binding
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        // Esta es la definición de nuestro adaptador personalizado.
        // Contiene la lista de valores
        // Y la vista que va a rellenar visualmente en la ui
        private final List<PlaceholderContent.PlaceholderItem> mValues;
        private final View mItemDetailFragmentContainer;


        // Constructor con parametros con la lista y la vista
        SimpleItemRecyclerViewAdapter(List<PlaceholderContent.PlaceholderItem> items,
                                      View itemDetailFragmentContainer) {
            mValues = items;
            mItemDetailFragmentContainer = itemDetailFragmentContainer;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Esto crea el hueco donde va a ir cada elemento.
            // Esto sigue el patron View Holder
            // Se llamada una vez por cada elemento haya en la lista de datos
            CharacterListContentBinding binding =
                    CharacterListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }



        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // esta función se llama una vez se ha creado el ViewHolder, y rellena realmente
            // los datos sobre ese view holder.
            // Se llamada una vez por cada elemento haya en la lista de datos
            holder.mIdView.setText(String.valueOf(mValues.get(position).id));
            holder.mContentView.setText(mValues.get(position).apodo);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(itemView -> {
                // Establecemos un listener de click, para que cuando haga pulsación sobre este elemento
                // nos movamos de fragment hacia el detalle. Pasamos el id del elemento como parametro
                // para luego saber sobre qué elemento se hizo click.
                PlaceholderContent.PlaceholderItem item =
                        (PlaceholderContent.PlaceholderItem) itemView.getTag();
                Bundle arguments = new Bundle();

                arguments.putString(CharacterDetailFragment.ARG_ITEM_ID, String.valueOf(item.id));


                if (mItemDetailFragmentContainer != null) {
                    Navigation.findNavController(mItemDetailFragmentContainer)
                            .navigate(R.id.fragment_character_detail, arguments);
                } else {
                    Navigation.findNavController(itemView).navigate(R.id.show_character_detail, arguments);
                }
            });

        }

        // Devuelve cuantos datos hay en la lista de datos
        // asi sabe cuantos view holder tiene que crear.
        @Override
        public int getItemCount() {
            return mValues.size();
        }


        // Modelamos qué va a contener nuestro View Holder
        // es decir, que contiene 1 fila de la lista
        class ViewHolder extends RecyclerView.ViewHolder {
            // Pues contiene un text view que correspondera con el id del personaje
            final TextView mIdView;
            // Y contendra el apodo
            final TextView mContentView;

            // El contructor espera el binding para saber con qué tiene que rellenar cada hueco
            ViewHolder(CharacterListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.idText;
                mContentView = binding.content;
            }

        }
    }
}