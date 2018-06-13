package sli.isaiahgao.data;

import java.util.HashMap;
import java.util.Map;

import sli.isaiahgao.Main;

public class HandlerRoomData {

    public HandlerRoomData(Main instance) {
        this.instance = instance;
        this.practiceRooms = new HashMap<>();
    }

    private Main instance;
    private Map<Integer, UserInstance> practiceRooms;

}
