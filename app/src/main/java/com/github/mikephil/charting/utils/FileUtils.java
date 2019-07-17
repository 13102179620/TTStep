
package com.github.mikephil.charting.utils;

/**
 * Utilities class for interacting with the assets and the devices storage to
 * load and save DataSet objects from and to .txt files.
 * 
 * @author Philipp Jahoda
 */
public class FileUtils {

    private static final String LOG = "MPChart-FileUtils";

    /**
     * Loads a an Array of Entries from a textfile from the sd-card.
     * 
     * @param path the name of the file on the sd-card (+ path if needed)
     * @return
     */
    public static java.util.List<com.github.mikephil.charting.data.Entry> loadEntriesFromFile(String path) {

        java.io.File sdcard = android.os.Environment.getExternalStorageDirectory();

        // Get the text file
        java.io.File file = new java.io.File(sdcard, path);

        java.util.List<com.github.mikephil.charting.data.Entry> entries = new java.util.ArrayList<com.github.mikephil.charting.data.Entry>();

        try {
            @SuppressWarnings("resource")
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");

                if (split.length <= 2) {
                    entries.add(new com.github.mikephil.charting.data.Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                } else {

                    float[] vals = new float[split.length - 1];

                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }

                    entries.add(new com.github.mikephil.charting.data.BarEntry(vals, Integer.parseInt(split[split.length - 1])));
                }
            }
        } catch (java.io.IOException e) {
            android.util.Log.e(LOG, e.toString());
        }

        return entries;

        // File sdcard = Environment.getExternalStorageDirectory();
        //
        // // Get the text file
        // File file = new File(sdcard, path);
        //
        // List<Entry> entries = new ArrayList<Entry>();
        // String label = "";
        //
        // try {
        // @SuppressWarnings("resource")
        // BufferedReader br = new BufferedReader(new FileReader(file));
        // String line = br.readLine();
        //
        // // firstline is the label
        // label = line;
        //
        // while ((line = br.readLine()) != null) {
        // String[] split = line.split("#");
        // entries.add(new Entry(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }

    /**
     * Loads an array of Entries from a textfile from the assets folder.
     * 
     * @param am
     * @param path the name of the file in the assets folder (+ path if needed)
     * @return
     */
    public static java.util.List<com.github.mikephil.charting.data.Entry> loadEntriesFromAssets(android.content.res.AssetManager am, String path) {

        java.util.List<com.github.mikephil.charting.data.Entry> entries = new java.util.ArrayList<com.github.mikephil.charting.data.Entry>();

        java.io.BufferedReader reader = null;
        try {
            reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split("#");

                if (split.length <= 2) {
                    entries.add(new com.github.mikephil.charting.data.Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                } else {

                    float[] vals = new float[split.length - 1];

                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }

                    entries.add(new com.github.mikephil.charting.data.BarEntry(vals, Integer.parseInt(split[split.length - 1])));
                }
                line = reader.readLine();
            }
        } catch (java.io.IOException e) {
            android.util.Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (java.io.IOException e) {
                    android.util.Log.e(LOG, e.toString());
                }
            }
        }

        return entries;

        // String label = null;
        // List<Entry> entries = new ArrayList<Entry>();
        //
        // BufferedReader reader = null;
        // try {
        // reader = new BufferedReader(
        // new InputStreamReader(am.open(path), "UTF-8"));
        //
        // // do reading, usually loop until end of file reading
        // label = reader.readLine();
        // String line = reader.readLine();
        //
        // while (line != null) {
        // // process line
        // String[] split = line.split("#");
        // entries.add(new Entry(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // line = reader.readLine();
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        //
        // } finally {
        //
        // if (reader != null) {
        // try {
        // reader.close();
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        // }
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }

    /**
     * Saves an Array of Entries to the specified location on the sdcard
     * 
     * @param ds
     * @param path
     */
    public static void saveToSdCard(java.util.List<com.github.mikephil.charting.data.Entry> entries, String path) {

        java.io.File sdcard = android.os.Environment.getExternalStorageDirectory();

        java.io.File saved = new java.io.File(sdcard, path);
        if (!saved.exists())
        {
            try
            {
                saved.createNewFile();
            } catch (java.io.IOException e)
            {
                android.util.Log.e(LOG, e.toString());
            }
        }
        try
        {
            // BufferedWriter for performance, true to set append to file flag
            java.io.BufferedWriter buf = new java.io.BufferedWriter(new java.io.FileWriter(saved, true));

            for (com.github.mikephil.charting.data.Entry e : entries) {

                buf.append(e.getVal() + "#" + e.getXIndex());
                buf.newLine();
            }

            buf.close();
        } catch (java.io.IOException e)
        {
            android.util.Log.e(LOG, e.toString());
        }
    }

    public static java.util.List<com.github.mikephil.charting.data.BarEntry> loadBarEntriesFromAssets(android.content.res.AssetManager am, String path) {

        java.util.List<com.github.mikephil.charting.data.BarEntry> entries = new java.util.ArrayList<com.github.mikephil.charting.data.BarEntry>();

        java.io.BufferedReader reader = null;
        try {
            reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split("#");

                entries.add(new com.github.mikephil.charting.data.BarEntry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));

                line = reader.readLine();
            }
        } catch (java.io.IOException e) {
            android.util.Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (java.io.IOException e) {
                    android.util.Log.e(LOG, e.toString());
                }
            }
        }

        return entries;

        // String label = null;
        // ArrayList<Entry> entries = new ArrayList<Entry>();
        //
        // BufferedReader reader = null;
        // try {
        // reader = new BufferedReader(
        // new InputStreamReader(am.open(path), "UTF-8"));
        //
        // // do reading, usually loop until end of file reading
        // label = reader.readLine();
        // String line = reader.readLine();
        //
        // while (line != null) {
        // // process line
        // String[] split = line.split("#");
        // entries.add(new Entry(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // line = reader.readLine();
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        //
        // } finally {
        //
        // if (reader != null) {
        // try {
        // reader.close();
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        // }
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }
}
