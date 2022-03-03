package com.david.potterapp.placeholder;

import com.david.potterapp.CharacterDetailHostActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
// Representa un hueco en la lista de datos
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<Integer, PlaceholderItem> ITEM_MAP = new HashMap<Integer, PlaceholderItem>();

    static {
        for (int i = 0; i < CharacterDetailHostActivity.charactersList.size(); i++){
            // añadimos PlaceHolderItem que representa un item de nuestra lista
            addItem(
                    new PlaceholderItem(
                            CharacterDetailHostActivity.charactersList.get(i).getId(),
                            CharacterDetailHostActivity.charactersList.get(i).getPersonaje(),
                            CharacterDetailHostActivity.charactersList.get(i).getApodo(),
                            CharacterDetailHostActivity.charactersList.get(i).isEsEstudianteDeHogwarts(),
                            CharacterDetailHostActivity.charactersList.get(i).getCasaDeHogwarts(),
                            CharacterDetailHostActivity.charactersList.get(i).getInterprete(),
                            CharacterDetailHostActivity.charactersList.get(i).getHijos(),
                            CharacterDetailHostActivity.charactersList.get(i).getImagen()
                    ));
        }
    }

    public static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {

        public final int id;
        public String character;
        public String apodo;
        public boolean isHogwartsStudent;
        public String HogwartsHome;
        public String actor;
        public List<String> childs;
        public String image;

        public PlaceholderItem(int id, String character, String apodo, boolean isHogwartsStudent, String hogwartsHome, String actor, List<String> childs, String image) {
            this.id = id;
            this.character = character;
            this.apodo = apodo;
            this.isHogwartsStudent = isHogwartsStudent;
            this.HogwartsHome = hogwartsHome;
            this.actor = actor;
            this.childs = childs;
            this.image = image;
        }

        @Override
        public String toString() {
            return "Character{" +
                    "id=" + id +
                    ", character='" + character + '\'' +
                    ", isHogwartsStudent=" + isHogwartsStudent +
                    ", HogwartsHome='" + HogwartsHome + '\'' +
                    ", actor='" + actor + '\'' +
                    ", childs=" + childs +
                    ", image='" + image + '\'' +
                    '}';
        }
    }
}