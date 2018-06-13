package sli.isaiahgao.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import sli.isaiahgao.Main;
import sli.isaiahgao.io.SheetsIO;

public class HandlerUserData {

    public HandlerUserData(Main instance) {
        this.instance = instance;
        this.database = new HashMap<>();
        this.databasePositions = new HashMap<>();
    }

    private Main instance;
    private Map<String, UserData> database;
    private Map<String, Integer> databasePositions;
    private int dbpos;

    public void load() {
        Sheets service = SheetsIO.getService();
        final String range = "A:E";
        
        try {
            ValueRange response = service.spreadsheets().values().get(instance.getDatabaseURL(), range).execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                System.out.println("Loading database users...");
                for (List<Object> row : values) {
                    UserData usd = UserData.fromSheetsRow(row);
                    database.put(usd.getHopkinsID(), usd);
                    databasePositions.put(usd.getHopkinsID(), ++dbpos);
                }
                System.out.println("Loaded " + database.size() + " registered users!");
            }
        } catch (Exception e) {
            System.err.println("Critical error: could not load database! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void push(UserData dat) {
        // line to replace; either add a new line, or update an old line
        int index = database.size() + 1;
        if (databasePositions.containsKey(dat.getHopkinsID())) {
            index = databasePositions.get(dat.getHopkinsID());
        }
        
        // save/update to map
        database.put(dat.getHopkinsID(), dat);
        
        // save to db
        List<List<Object>> values = Arrays.asList(
                dat.toObjectList()
        );
        String range = "A" + index + ":E" + index;
        ValueRange body = new ValueRange().setValues(values);
        try {
            SheetsIO.getService().spreadsheets().values()
                .update(instance.getDatabaseURL(), range, body)
                .setValueInputOption("RAW")
                .execute();
        } catch (IOException e) {
            System.err.println("Failed to write cells.");
            e.printStackTrace();
        }
    }

}
