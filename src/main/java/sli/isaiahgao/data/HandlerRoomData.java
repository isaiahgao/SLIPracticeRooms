package sli.isaiahgao.data;

import java.io.File;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimerTask;

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
        SheetsIO.refreshService();
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
        this.disabledRooms = new HashMap<>();
        
        Main.TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HandlerRoomData.this.currentUsers.forEach((s, u) -> {
                    HandlerRoomData.this.instance.getBaseGUI().setTimeForRoom(u.getRoom(), u.getTimeRemaining());
                });
            }
        }, 1l, 30000l);
    }

    private Main instance;
    
    // user string id : userinstance
    private Map<String, UserInstance> currentUsers;
    // room : reason for disabling
    private Map<Integer, String> disabledRooms;
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
            instance.getBaseGUI().setTimeForRoom(inst.getRoom(), null);
            instance.sendDisappearingConfirm("Returned<br>Practice Room " + inst.getRoom() + "!", 115);
            
            Sound.SIGN_OUT.play();
            return ActionResult.LOG_OUT;
        }
        
        this.login(usd, room);
        instance.getBaseGUI().getButtonByID(room).setEnabled(false);
        instance.sendDisappearingConfirm("Checked out<br>Practice Room " + this.instance.getBaseGUI().getPressedButtonID() + "!", 115);
        Sound.SIGN_IN.play();
        return ActionResult.LOG_IN;
    }
    
    public synchronized UserInstance getUserInstance(String id) {
        return this.currentUsers.get(id);
    }
    
    public synchronized boolean usingRoom(String id) {
        return this.currentUsers.containsKey(id);
    }
    
    public void disableRoom(int room, String reason) {
        this.disabledRooms.put(room, reason);
    }
    
    public synchronized void removeRoom(int room) {
        this.disabledRooms.remove(room);
        for (Iterator<UserInstance> it = currentUsers.values().iterator(); it.hasNext();) {
            if (it.next().getRoom() == room) {
                it.remove();
                break;
            }
        }
    }

    // sync program to spreadsheet
    @Deprecated
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
            Set<Integer> checkedIn = new HashSet<>();
            // reverse iterate through excel sheet
            // find all the rooms that have been checked in
            for (int j = values.size() - 1; j > 0; j--) {
                List<Object> list = values.get(j);
                if (!list.isEmpty() && !this.isEmpty(list.get(0))) {
                    String[] arr = ((String) list.get(4)).split(" ");
                    // too short to be worthwhile
                    if (arr.length < 2) {
                        continue;
                    }
                    
                    // get room number
                    int room = Integer.parseInt(arr[1]);
                    // check if already processed before
                    if (!processed.add(room)) {
                        continue;
                    }
                    
                    // check if still checked out
                    if (list.size() < 9 || this.isEmpty(list.get(8))) {
                        continue;
                    }

                    System.out.println("still checked in: " + room);
                    checkedIn.add(room);
                }
            }
            
            // remove the checked-in ones from currentUsers
            for (Iterator<UserInstance> it = this.currentUsers.values().iterator(); it.hasNext();) {
                UserInstance uis = it.next();
                int room = uis.getRoom();
                if (checkedIn.remove(room) && !this.disabledRooms.containsKey(room)) {
                    it.remove();
                    
                    // set button active again
                    this.instance.getBaseGUI().getButtonByID(room).setEnabled(true);
                    this.instance.getBaseGUI().setTimeForRoom(room, null);
                }
            }
            
            this.writeCurrentUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadUser(String str) {
        try {
            UserInstance inst = new UserInstance(str);
            this.currentUsers.put(inst.getUser().getHopkinsID(), inst);
            this.instance.getBaseGUI().getButtonByID(inst.getRoom()).setEnabled(false);
            this.instance.getBaseGUI().setTimeForRoom(inst.getRoom(), inst.getTimeRemaining());
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
        instance.getBaseGUI().setTimeForRoom(room, inst.getTimeRemaining());
        this.push(inst);
    }
    
    // log out
    private synchronized void logout(UserInstance inst) {
        currentUsers.remove(inst.getUser().getHopkinsID());
        this.poll(inst);
    }
    
    // fill in Time In and Monitor Initials in database
    private synchronized void poll(UserInstance inst) {
        Action action = () -> {
            Spreadsheets accessor = SheetsIO.getService().spreadsheets();
            String range = inst.getSheetName() + "!H" + inst.getLine() + ":I" + inst.getLine();
            
            List<List<Object>> values = Arrays.asList(Arrays.asList(
                    Utils.getTime(new Date()), "AUTO LOG"
                    ));
            ValueRange vr = new ValueRange().setValues(values);
            
            accessor.values().update(Main.getLogURL(), range, vr)
            .setValueInputOption("RAW")
            .execute();
        };
        
        Main.getActionThread().addAction(action);
        this.writeCurrentUsers();
    }
    
    // handle IO for logging in
    private synchronized void push(UserInstance inst) {
        this.checkMonth();
        this.logUser(inst);
    }
    
    // fill in user data
    private synchronized void logUser( UserInstance inst) {
        Action action = () -> {
            Spreadsheets accessor = SheetsIO.getService().spreadsheets();
            
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
            
            accessor.values()
                .update(Main.getLogURL(), range, body)
                .setValueInputOption("RAW")
                .execute();
        };
        
        Main.getActionThread().addAction(action);
        this.writeCurrentUsers();
    }
    
    // check if a new month is needed
    private synchronized void checkMonth() {
        Action bulk = () -> {
            Spreadsheets accessor = SheetsIO.getService().spreadsheets();
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
    
    // loads data from config file
    public void loadCurrentRooms() {
        try {
            File file = new File("config.jhunions");
            if (!file.exists())
                return;
            
            Scanner sc = new Scanner(file);
            List<String> data = new LinkedList<>();
            while (sc.hasNextLine()) {
                data.add(sc.nextLine());
            }
            sc.close();
            
            boolean disabled = false;
            for (String s : data) {
                if (disabled) {
                    String[] arr = s.split(" ");
                    int room = Integer.parseInt(arr[0]);
                    String reason = arr[1];
                    for (int i = 2; i < arr.length; i++) {
                        reason += " " + arr[i];
                    }
                    
                    this.disabledRooms.put(room, reason);
                    this.instance.getBaseGUI().getButtonByID(room).setEnabled(false);
                    this.instance.getBaseGUI().setTimeForRoom(room, reason);
                } else {
                    if (s.equals("DISABLED ROOMS")) {
                        disabled = true;
                        continue;
                    }
                    loadUser(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // writes current state to config file
    public synchronized void writeCurrentUsers() {
        try {
            if (this.currentUsers.isEmpty() && this.disabledRooms.isEmpty()) {
                File file = new File("config.jhunions");
                if (file.exists()) {
                    file.delete();
                }
                return;
            }
            
            FileWriter writer = new FileWriter("config.jhunions");
            this.currentUsers.entrySet().stream().forEach((entry) -> {
                try {
                    writer.write(entry.getValue().toString() + System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            if (!this.disabledRooms.isEmpty()) {
                writer.write("DISABLED ROOMS");
                writer.write(System.lineSeparator());
                this.disabledRooms.forEach((i, s) -> {
                    try {
                        writer.write(i + " " + s + System.lineSeparator());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
