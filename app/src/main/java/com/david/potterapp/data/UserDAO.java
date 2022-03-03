package com.david.potterapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.david.potterapp.data.model.User;

import java.util.ArrayList;

// representacion de nuestro patron de acceso a los datos de un usuario
public class UserDAO {
    Context c;
    User u;
    ArrayList<User> lista;
    SQLiteDatabase sql;
    String bd = "HarryPotterDB";
    String createTable = "create table if not exists user(id integer primary key autoincrement,username text, password text)";
    String insertFirstUser = "INSERT OR IGNORE INTO user(id, username, password) VALUES(999, 'hermione@harrypotter.com', '123456')";

    // constructor
    public UserDAO(Context c) {
        this.c = c;
        sql = c.openOrCreateDatabase(bd, c.MODE_PRIVATE, null);
        sql.execSQL(createTable);
        sql.execSQL(insertFirstUser);
        u = new User();
    }

    // inserta un usuario en la bd
    public boolean insertUser(User u) {
        if (buscar(u.getUsername()) == 0) {
            ContentValues cv = new ContentValues();
            cv.put("username", u.getUsername());
            cv.put("password", u.getPassword());
            return (sql.insert("user", null, cv) > 0);
        } else {
            return false;
        }
    }

    // busca un usuario por su username
    public int buscar(String u) {
        int x = 0;
        lista = selectUsuario();
        for (User us : lista) {
            if (us.getUsername().equals(u)) {
                x++;
            }
        }
        return x;
    }

    // devuelve toda la lista de usuarios
    public ArrayList<User> selectUsuario() {
        ArrayList<User> lista = new ArrayList<User>();
        lista.clear();
        Cursor cr = sql.rawQuery("select * from user", null);
        if (cr != null && cr.moveToFirst()) {
            do {
                User u = new User();
                u.setId(cr.getInt(0));
                u.setUsername(cr.getString(1));
                u.setPassword(cr.getString(2));
                lista.add(u);
            } while (cr.moveToNext());

        }
        return lista;
    }

    // busca por username y password en al bd. Si coincide, el login es satisfactorio
    public boolean login(String u, String p) {
        int a = 0;
        Cursor cr = sql.rawQuery("select * from user", null);
        if (cr != null && cr.moveToFirst()) {
            do {
                if (cr.getString(1).equals(u) && cr.getString(2).equals(p)) {
                    a++;
                }
            } while (cr.moveToNext());

        }
        return a==1;
    }

    // Obtiene un usuario de la base de datos
    public User getUser(String us, String p){
        lista=selectUsuario();
        for (User u:lista){
            if(u.getUsername().equals(us)&&u.getPassword().equals(p)){
                return u;
            }
        }
        return null;
    }

    // busca un usuario por su id
    public User getUserById(int id){
        lista=selectUsuario();
        for (User u:lista){
            if(u.getId()==id){
                return u;
            }
        }
        return null;
    }
}
