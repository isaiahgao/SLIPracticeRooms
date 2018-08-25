package sli.isaiahgao.data;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import sli.isaiahgao.SoundHandler.Sound;
import sli.isaiahgao.Utils;
import sli.isaiahgao.io.Action;
import sli.isaiahgao.io.QueueIO.IODestination;
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
    private volatile int logsize;
    private Month month;
    
    private synchronized void incLog() {
        this.logsize++;
    }
    
    /**
     * Handle an ID scan.
     * @param id The user's string ID.
     * @param room The room the user is checking out, or 0 if none.
     * @return Result of action, either LOG_IN or LOG_OUT.
     */
    public synchronized ActionResult scan(UserData usd, int room) {
        UserInstance inst = this.currentUsers.get(usd.getHopkinsID());
        if (inst != null) {
            this.logout(inst);
            instance.getBaseGUI().getButtonByID(inst.getRoom()).setEnabled(true);
            Sound.SIGN_OUT.play();
            return ActionResult.LOG_OUT;
        }
        
        this.login(usd, room);
        instance.getBaseGUI().getButtonByID(room).setEnabled(false);
        Sound.SIGN_IN.play();
        return ActionResult.LOG_IN;
    }
    
    public synchronized UserInstance getUserInstance(String id) {
        return this.currentUsers.get(id);
    }
    
    public synchronized boolean usingRoom(String id) {
        return this.currentUsers.containsKey(id);
    }

    // sync program to spreadsheet
    public synchronized void synchronize() {
        try {
            ValueRange vr = SheetsIO.getService().spreadsheets().values().get(Main.getLogURL(), "A:I").execute();
            List<List<Object>>  values = vr.getValues();
            if (values == null) {
                this.logsize = 0;
                return;
            }
            
            // calculate log size
            this.logsize = 0;
            values.stream().forEach((list) -> {
                if (!list.isEmpty() && !this.isEmpty(list.get(0))) {
                    ++this.logsize;
                }
            });
            
            Set<Integer> processed = new HashSet<>();
            // reverse iterate
            for (int j = values.size() - 1; j > 0; j--) {
                List<Object> list = values.get(j);
                if (!list.isEmpty() && !this.isEmpty(list.get(0))) {
                    if (list.size() < 9 || this.isEmpty(list.get(8))) {
                        // still checked out; don't care
                        return;
                    }
                    
                    // manually marked as returned;
                    // 1. set button active again
                    // 2. remove user from room data
                    String[] arr = ((String) list.get(4)).split(" ");
                    if (arr.length < 2) {
                        return;
                    }
                    
                    int room = Integer.parseInt(arr[1]);
                    
                    if (!processed.add(room)) {
                        // already processed this room number
                        return;
                    }
                    
                    // remove user from map
                    for (Iterator<UserInstance> it = this.currentUsers.values().iterator(); it.hasNext();) {
                        UserInstance uis = it.next();
                        if (uis.getRoom() == room) {
                            it.remove();
                            
                            // set button active again
                            this.instance.getBaseGUI().getButtonByID(room).setEnabled(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadUser(String str) {
        try {
            UserInstance inst = new UserInstance(str);
            this.currentUsers.put(inst.getUser().getHopkinsID(), inst);
            this.instance.getBaseGUI().getButtonByID(inst.getRoom()).setEnabled(false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isEmpty(Object o) {
        return o == null || o.equals("");
    }
    
    // log in
    private synchronized void login(UserData usd, int room) {
        UserInstance inst = new UserInstance(usd, room);
        currentUsers.put(usd.getHopkinsID(), inst);
        this.push(inst);
    }
    
    // log out
    private synchronized void logout(UserInstance inst) {
        currentUsers.remove(inst.getUser().getHopkinsID());
        this.poll(inst);
    }
    
    // fill in Time In and Monitor Initials in database
    private synchronized void poll(UserInstance inst) {
        Spreadsheets accessor = SheetsIO.getService().spreadsheets();
        String range = inst.getSheetName() + "!H" + inst.getLine() + ":I" + inst.getLine();
        
        List<List<Object>> values = Arrays.asList(Arrays.asList(
                Utils.getTime(new Date()), "AUTO LOG"
                ));
        ValueRange vr = new ValueRange().setValues(values);
        Action action = () -> {
            accessor.values().update(Main.getLogURL(), range, vr)
            .setValueInputOption("RAW")
            .execute();
        };
        
        Main.getActionThread().addAction(action);
        this.writeCurrentUsers();
    }
    
    // handle IO for logging in
    private synchronized void push(UserInstance inst) {
        Spreadsheets accessor = SheetsIO.getService().spreadsheets();
        this.checkMonth(accessor);
        this.logUser(accessor, inst);
    }
    
    // fill in user data
    private synchronized void logUser(Spreadsheets accessor, UserInstance inst) {
        if (this.logsize == 0) {
            try {
                ValueRange vr = accessor.values().get(Main.getLogURL(), "A:A").execute();
                this.logsize = vr.getValues() == null ? 0 : vr.getValues().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.incLog();
        // save line that we're putting data on
        inst.setLine(this.logsize);
        
        
        // save to db
        List<List<Object>> values = Arrays.asList(
                inst.toObjectList()
        );
        String range = "A" + this.logsize + ":J" + this.logsize;
        ValueRange body = new ValueRange().setValues(values);
        Action action = () -> {
            accessor.values()
                .update(Main.getLogURL(), range, body)
                .setValueInputOption("RAW")
                .execute();
        };
        
        Main.getActionThread().addAction(action);
        this.writeCurrentUsers();
    }
    
    // check if a new month is needed
    private synchronized void checkMonth(Spreadsheets accessor) {
        Action bulk = () -> {
            if (this.month == null || Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1) != this.month) {
                // see if we need to create a new spreadsheet
                this.month = Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1);
                String matching = Utils.capitalizeFirst(this.month.toString()) + " " + Calendar.getInstance().get(Calendar.YEAR);
                
                Spreadsheet ss = accessor.get(Main.getLogURL()).execute();
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
                
                // create the action
                Action action = () -> {
                    accessor.batchUpdate(Main.getLogURL(), new BatchUpdateSpreadsheetRequest().setRequests(req)).execute();
    
                    accessor.values()
                        .update(Main.getLogURL(), range, body)
                        .setValueInputOption("RAW")
                        .execute();
                };
                
                try {
                    action.run();
                } catch (IOException e) {
                    System.err.println("Connection failed; attempting to re-establish...");
                    Main.getIOQueue().pushRequest(IODestination.LOG, action);
                }
            }
        };
        Main.getActionThread().addAction(bulk);
        this.writeCurrentUsers();
    }
    
    public synchronized void writeCurrentUsers() {
        try {
            FileWriter writer = new FileWriter("config.jhunions");
            this.currentUsers.entrySet().stream().forEach((entry) -> {
                try {
                    writer.write(entry.getValue().toString() + System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
