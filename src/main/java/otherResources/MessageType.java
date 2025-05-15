package otherResources;

public enum MessageType {
    //scheduler -> elevator:
    TRAVEL_TO_FLOOR,
    MOVE, //the move command should be sent repeatedly after each CURRENT_FLOOR_UPDATE until elevator should stop
    STOP,
    OPEN_DOOR,
    CLOSE_DOOR,
    REQUEST_CURRENT_FLOOR_UPDATE,
    REQUEST_STOPPED_CONFIRMATION,
    REQUEST_UNLOADING_COMPLETE_CONFIRMATION,
    FAULT_DOOR, //scheduler sends this to elevator to initiate a door fault (at a floor, soft fault)
    FAULT_STUCK, //scheduler sends this to elevator to initiate a stuck fault (in between floors, hard fault)
    OUT_OF_ORDER, //scheduler sends this to elevator for a hard fault in response to REPORT_STUCK



    //floor -> scheduler:
    INITIAL_READ_FROM_FILE, //the initial message as read from file


    //elevator -> scheduler:
    ACK_TRAVEL_TO_FLOOR, //acknowledge that the TRAVEL_TO_FLOOR message was received
    CONFIRM_STOPPED,
    CONFIRM_DOOR_OPENED,
    CONFIRM_DOOR_CLOSED,
    INITIAL_FLOOR_UPDATE,
    CURRENT_FLOOR_UPDATE, //elevator sends the current floor update to scheduler as it is moving,
    CONFIRM_LOADING_UNLOADING_COMPLETE,
    REPORT_DOOR_NOT_CLOSING, //elevator sends this to scheduler in response to FAULT_DOOR
    REPORT_DOOR_NOT_OPENING, //elevator sends this to scheduler in response to FAULT_DOOR
    REPORT_STUCK, //elevator sends this to scheduler in response to FAULT_STUCK


    //default type
    NONE
}