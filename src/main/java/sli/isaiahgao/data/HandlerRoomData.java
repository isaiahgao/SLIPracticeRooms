package sli.isaiahgao.data;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;

import sli.isaiahgao.Main;
import sli.isaiahgao.Utils;
import sli.isaiahgao.io.SheetsIO;

public class HandlerRoomData {
    
    public static void main(String[] args) throws Exception {
        SheetsIO.init();
        HandlerRoomData a = new HandlerRoomData(new Main());
        a.login(new UserData("1654535123", new FullName("Bob", "Bobbinson"), "bbob1", 1346587513l), 112);
        
        Thread.sleep(5000);
        a.logout(a.currentUsers.get("1654535123"));
    }
    
    public enum ActionResult {
        LOG_OUT,
        LOG_IN;
    }

    public HandlerRoomData(Main instance) {
        this.instance = instance;
        this.currentUsers = new HashMap<>();
    }

    private Main instance;
    
    // user string id : userinstance
    private Map<String, UserInstance> currentUsers;
    private int logsize;
    private Month month;
    
    /**
     * Handle an ID scan.
     * @param id The user's string ID.
     * @param room The room the user is checking out, or 0 if none.
     * @return Result of action, either LOG_IN or LOG_OUT.
     */
    public ActionResult scan(UserData usd, int room) {
        UserInstance inst = this.currentUsers.get(usd.getHopkinsID());
        if (inst != null) {
            this.logout(inst);
            return ActionResult.LOG_OUT;
        }
        
        this.login(usd, room);
        return ActionResult.LOG_IN;
    }
    
    public boolean usingRoom(String id) {
        return this.currentUsers.containsKey(id);
    }
    
    // log in
    private void login(UserData usd, int room) {
        UserInstance inst = new UserInstance(usd, room);
        currentUsers.put(usd.getHopkinsID(), inst);
        try {
            this.push(inst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // log out
    private void logout(UserInstance inst) {
        currentUsers.remove(inst.getUser().getHopkinsID());
        this.poll(inst);
    }
    
    // fill in Time In and Monitor Initials in database
    private void poll(UserInstance inst) {
        Spreadsheets accessor = SheetsIO.getService().spreadsheets();
        String range = inst.getSheetName() + "!H" + inst.getLine() + ":I" + inst.getLine();
        
        List<List<Object>> values = Arrays.asList(Arrays.asList(
                Utils.getTime(new Date()), "AUTO LOG"
                ));
        ValueRange vr = new ValueRange().setValues(values);
        try {
            accessor.values().update(instance.getLogURL(), range, vr)
                .setValueInputOption("RAW")
                .execute();
        } catch (IOException e) {
            System.err.println("Connection failed; attempting to re-establish...");
            e.printStackTrace();
            // TODO re-establish connection and queue the data push
        }
    }
    
    // handle IO for logging in
    private void push(UserInstance inst) throws IOException {
        Spreadsheets accessor = SheetsIO.getService().spreadsheets();
        this.checkMonth(accessor);
        this.logUser(accessor, inst);
    }
    
    // fill in user data
    private void logUser(Spreadsheets accessor, UserInstance inst) {
        if (this.logsize == 0) {
            try {
                ValueRange vr = accessor.values().get(instance.getLogURL(), "A:A").execute();
                this.logsize = vr.getValues() == null ? 0 : vr.getValues().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ++this.logsize;
        // save line that we're putting data on
        inst.setLine(this.logsize);
        
        
        // save to db
        List<List<Object>> values = Arrays.asList(
                inst.toObjectList()
        );
        String range = "A" + this.logsize + ":J" + this.logsize;
        ValueRange body = new ValueRange().setValues(values);
        try {
            accessor.values()
                .update(instance.getLogURL(), range, body)
                .setValueInputOption("RAW")
                .execute();
        } catch (IOException e) {
            System.err.println("Connection failed; attempting to re-establish...");
            // TODO re-establish connection and queue the data push
        }
    }
    
    // check if a new month is needed
    private void checkMonth(Spreadsheets accessor) throws IOException {
        if (this.month == null || Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1) != this.month) {
            // see if we need to create a new spreadsheet
            this.month = Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1);
            String matching = Utils.capitalizeFirst(this.month.toString()) + " " + Calendar.getInstance().get(Calendar.YEAR);
            
            Spreadsheet ss = accessor.get(instance.getLogURL()).execute();
            for (Sheet s : ss.getSheets()) {
                if (s.getProperties().getTitle().equals(matching)) {
                    // we have a sheet for the current month; proceed as usual
                    return;
                }
            }
            
            //otherwise create the new sheet
            
            // generate properties
            SheetProperties prop = new SheetProperties();
            prop.setTitle(matching);
            prop.setIndex(0);
            
            int id = this.month.getValue() << 24 | Calendar.getInstance().get(Calendar.YEAR);
            prop.setSheetId(id);
            
            List<Request> req = new ArrayList<>();
            req.add(new Request().setAddSheet(new AddSheetRequest().setProperties(prop)));
            
            try {
                accessor.batchUpdate(instance.getLogURL(), new BatchUpdateSpreadsheetRequest().setRequests(req)).execute();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // write the header data
            List<List<Object>> values = new ArrayList<>();
            values.add(Lists.newArrayList(
                    "Timestamp",
                    "Name",
                    "JHED E-mail",
                    "Phone Number",
                    "Room",
                    "Current Time",
                    "Agreement",
                    "Time Returned",
                    "Monitor Name Upon Return",
                    "Comments"
                    ));
            String range = matching + "!A1:J1";
            ValueRange body = new ValueRange().setValues(values);
            try {
                accessor.values()
                    .update(instance.getLogURL(), range, body)
                    .setValueInputOption("RAW")
                    .execute();
            } catch (IOException e) {
                System.err.println("Connection failed; attempting to re-establish...");
                // TODO re-establish connection and queue the data push
            }
        }
    }
    
}
