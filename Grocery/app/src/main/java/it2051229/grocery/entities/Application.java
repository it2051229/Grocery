package it2051229.grocery.entities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Application {

    /**
     * Global date common formatting
     */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");

    /**
     * Global request codes for activities
     */
    public static final int ADD_GROCERY_ITEM_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_GROCERY_ITEM_ACTIVITY_REQUEST_CODE = 2;
    public static final int VIEW_FILTERED_LIST_ACTIVITY_REQUEST_CODE = 3;
    public static final int ADD_OR_REDUCE_COST_ACTIVITY_REQUEST_CODE = 4;

    /**
     * Global location of grocery related files
     */
    public static final File DIRECTORY = new File(Environment.getExternalStorageDirectory().toString() + "/Grocery");

    /**
     * Write the grocery object to an external file
     */
    public static void saveData(Context context, Grocery grocery) {
        try {
            FileOutputStream fos = context.openFileOutput("data.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(grocery);

            oos.close();
            fos.close();
        } catch(Exception e) {
            Log.e("saveData()", e.getMessage());
        }
    }
}
