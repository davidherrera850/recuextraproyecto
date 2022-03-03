package com.david.potterapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.david.potterapp.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// representa nuestro patro de acceso a los datos de los personajes
public class CharacterDAO {
    Context context;
    String createTable = "create table if not exists character (id integer primary key autoincrement, personaje text, apodo text, estudiante boolean not null check(estudiante in (0,1)), casa text, interprete text, hijos text, imagen text)";
    SQLiteDatabase sql;
    String bd = "HarryPotterDB";

    // constructor. Necesita un contexto de ejecución
    public CharacterDAO(Context c) {
        this.context = c;
        sql = c.openOrCreateDatabase(bd, c.MODE_PRIVATE, null);
        sql.execSQL(createTable);
    }

    // comprueba si la tabla está vacía o no
    public boolean checkIfEmptyTable() {
        Cursor cr = sql.rawQuery("select COUNT(*) from character", null);
        boolean empty = true;
        if (cr != null && cr.moveToFirst()) {
            empty = (cr.getInt(0) == 0);
        }
        cr.close();
        return empty;
    }

    // comprueba si un personaje existe o no en la BD
    public boolean checkIfExist(String personaje) {
        Cursor cr = sql.rawQuery("select * from character", null);
        if (cr != null && cr.moveToFirst()) {
            do {
                if (cr.getString(1).equals(personaje)) {
                    return true;
                }
            } while (cr.moveToNext());
        }
        return false;
    }

    // Guarda un personaje dentro de la base de datos
    public boolean save(Character character) {
        if (!checkIfExist(character.getPersonaje())) {
            ContentValues cv = new ContentValues();

            // si el id no es -1 es porque ya tenia id
            // es decir viene de la api
            if(character.getId() != -1){
                cv.put("id", character.getId());
            }


            cv.put("personaje", character.getPersonaje());
            cv.put("apodo", character.getApodo());
            cv.put("estudiante", character.isEsEstudianteDeHogwarts());
            cv.put("casa", character.getCasaDeHogwarts());
            cv.put("interprete", character.getInterprete());
            if (character.getHijos() != null){
                cv.put("hijos", character.getHijos().toString().toString());
            }

            cv.put("imagen", character.getImagen());

            return (sql.insert("character", null, cv) > 0);
        }
        return false;
    }

    // Borra un personaje de la bd
    public void deleteCharacter(String personaje) {

        System.out.println("Buscando para borrar");
        int idToDelete = -1;
        Cursor cr = sql.rawQuery("select * from character", null);
        if (cr != null && cr.moveToFirst()) {
            do {
                if (cr.getString(1).equals(personaje)) {
                    idToDelete = cr.getInt(0);
                    System.out.println("Encontrado" + idToDelete);
                }
            } while (cr.moveToNext());
        }
        if (idToDelete != -1) {
            System.out.println("Buscando para  - 1");
            sql.execSQL("DELETE from character where id=" + idToDelete +"");
        }

    }

    // Obtiene el siguiente id (autoincrement) de la bd
    public int getLastId(){
        Cursor c = null;
        long seq = 0;
        try {
            String sql_str = "select seq from sqlite_sequence where name=?";
            c = sql.rawQuery(sql_str, new String[] {"character"});
            if (c.moveToFirst()) {
                seq = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return (int) (seq + 1);
    }

    // recorre la tabla usuarios y devuelve una lista con los usuarios
    public ArrayList<Character> getCharacters() {
        ArrayList<Character> lista = new ArrayList<Character>();
        lista.clear();
        Cursor cr = sql.rawQuery("select * from character", null);
        if (cr != null && cr.moveToFirst()) {
            // para cada posicion del cursor
            do {
                // creo un personaje vacio y empiezo a rellenar
                Character  c = new Character();
                c.setId(cr.getInt(0));
                c.setPersonaje(cr.getString(1));
                c.setApodo(cr.getString(2));
                c.setEsEstudianteDeHogwarts(cr.getInt(3) == 0? false: true);
                c.setCasaDeHogwarts(cr.getString(4));
                c.setInterprete(cr.getString(5));

                String lines[] = cr.getString(6).split("\\r?\\n");
                List<String> hijos_final = Arrays.asList(lines);
                c.setHijos(hijos_final);
                c.setImagen(cr.getString(7));

                lista.add(c);
            } while (cr.moveToNext());

        }
        return lista;
    }

}
