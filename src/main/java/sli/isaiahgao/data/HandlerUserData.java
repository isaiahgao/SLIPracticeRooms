package sli.isaiahgao.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteRangeRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
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
    
    public UserData getUserData(String id) {
        return database.get(id);
    }
    
    public void removeUserData(String id) {
        if (this.database.remove(id) != null) {
            int j = databasePositions.get(id);
            
            try {
            List<Request> requests = new ArrayList<>();
            GridRange range = new GridRange().setStartRowIndex(j).setEndRowIndex(j);
            requests.add(new Request().setDeleteRange(new DeleteRangeRequest().
                    setRange(range).setShiftDimension("ROWS")));
            
            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            SheetsIO.getService().spreadsheets().batchUpdate(this.instance.getDatabaseURL(), body).execute();
            
            databasePositions.remove(id);
            --this.dbpos;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // load database from excel document
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
            System.err.println("Connection failed; attempting to re-establish...");
            // TODO re-establish connection and queue the data push
        }
    }

}
