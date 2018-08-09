package sli.isaiahgao.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteRangeRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;

import sli.isaiahgao.Main;
import sli.isaiahgao.io.Action;
import sli.isaiahgao.io.SheetsIO;

public class HandlerUserData {
    
    /*
     * ERROR CASES:
     * attempt to push data, but connection is lost or no internet exists
     *      put action into a queue, and ping queue every now and then
     * 
     */

    public HandlerUserData(Main instance) {
        this.instance = instance;
        this.database = new HashMap<>();
        this.databasePositions = new HashMap<>();
    }

    private Main instance;
    private Map<String, UserData> database;
    private Map<String, Integer> databasePositions;
    private volatile int dbpos;

    private synchronized int decdbpos() {
        this.dbpos--;
        return this.dbpos;
    }
    
    private synchronized int incdbpos() {
        this.dbpos++;
        return this.dbpos;
    }
    
    public synchronized UserData getUserData(String id) {
        return database.get(id);
    }
    
    public synchronized void removeUserData(String id) {
        if (this.database.remove(id) != null) {
            int j = databasePositions.get(id);
            
            Action action = () -> {
                List<Request> requests = new ArrayList<>();
                GridRange range = new GridRange().setStartRowIndex(j).setEndRowIndex(j);
                requests.add(new Request().setDeleteRange(new DeleteRangeRequest().
                        setRange(range).setShiftDimension("ROWS")));
                
                BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                SheetsIO.getService().spreadsheets().batchUpdate(Main.getDatabaseURL(), body).execute();
                
                databasePositions.remove(id);
                this.decdbpos();
            };

            Main.getActionThread().addAction(action);
        }
    }
    
    // load database from excel document
    // not synchronized bc this only runs once
    public void load() {
        Sheets service = SheetsIO.getService();
        final String range = "A:E";
        
        try {
            ValueRange response = service.spreadsheets().values().get(Main.getDatabaseURL(), range).execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                System.out.println("Loading database users...");
                for (List<Object> row : values) {
                    UserData usd = UserData.fromSheetsRow(row);
                    database.put(usd.getHopkinsID(), usd);
                    databasePositions.put(usd.getHopkinsID(), this.incdbpos());
                    System.out.println(usd.toString());
                }
                System.out.println("Loaded " + database.size() + " registered users!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Critical error: could not load database! Check internet connection and try again.");
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    public synchronized void push(UserData dat) {
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
        
        Action action = () -> {
            SheetsIO.getService().spreadsheets().values()
                .update(Main.getDatabaseURL(), range, body)
                .setValueInputOption("RAW")
                .execute();
        };
        
        Main.getActionThread().addAction(action);
    }

}
