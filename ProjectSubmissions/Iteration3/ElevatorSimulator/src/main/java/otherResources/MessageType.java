package otherResources;

public enum MessageType {
    //scheduler -> elevator:
    TRAVEL_TO_FLOOR,
    MOVE, //the move command should be sent repeatedly after each CURRENT_FLOOR_UPDATE until elevator should stop
    STOP,
    OPEN_DOOR,
    CLOSE_DOOR,
    REQUEST_INITIAL_FLOOR_UPDATE,
    REQUEST_CURRENT_FLOOR_UPDATE,
    REQUEST_STOPPED_CONFIRMATION,
    REQUEST_UNLOADING_COMPLETE_CONFIRMATION,


    //floor -> scheduler:
    INITIAL_READ_FROM_FILE, //the initial message as read from file


    //elevator -> scheduler:
    ACK_TRAVEL_TO_FLOOR, //acknowledge that the TRAVEL_TO_FLOOR message was received
    CONFIRM_MOVING,
    CONFIRM_STOPPED,
    CONFIRM_DOOR_OPENED,
    CONFIRM_DOOR_CLOSED,
    INITIAL_FLOOR_UPDATE,
    CURRENT_FLOOR_UPDATE, //elevator sends the current floor update to scheduler as it is moving,
    CONFIRM_LOADING_UNLOADING_COMPLETE,


    //default type
    NONE
}