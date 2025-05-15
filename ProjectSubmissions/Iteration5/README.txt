
# SYSC 3303 Group Project - Elevator Control System
## Group 1
## Project contributors
- Tristan Demers
- Toman Aleksiev
- Kenji Isak Laguan
- Steven Johnson
- Irina Ionescu

## About
This project simulates an elevator control system. It is a multithreaded Java application 
that includes simulated floors, elevators and a scheduler. 
User actions (e.g. calling an elevator, requesting to go to a specific floor, etc.) are simulated 
by reading instructions from a text file.

## List of files
For more documentation, see javadoc/
To see the Javadoc, open javadoc/index.html in your browser
ElevatorSimulator
├── README.txt
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── allSystems
│   │   │   │   ├── Elevator.java               - elevator threads class, runs independently with its own main(), sends and receives messages from Mailbox                                         
│   │   │   │   ├── Floor.java                  - floor threads class, runs independently with its own main(), sends and receives messages from Mailbox
│   │   │   │   ├── Mailbox.java                - class that receives messages, puts them in separate 
│   │   │   │   └── Scheduler.java              - scheduler thread class, runs independently with its own main(), queues (1 per thread) according to recipient, and releases them to the recipient
│   │   │   ├── elevStateMachine                -  the different states for the elevators as they cycle through 
│   │   │   │   ├── DoorClosing.java
│   │   │   │   ├── DoorOpened.java
│   │   │   │   ├── DoorOpening.java
│   │   │   │   ├── ElevatorState.java
│   │   │   │   ├── Idle.java
│   │   │   │   ├── Moving.java
│   │   │   │   ├── OutOfOrder.java
│   │   │   │   ├── Stopped.java
│   │   │   │   └── WaitingToMove.java
│   │   │   ├── gui                             - the gui files
│   │   │   │   ├── ElevatorCell.java
│   │   │   │   ├── FloorCell.form
│   │   │   │   ├── FloorCell.java
│   │   │   │   ├── elevatorCell.form
│   │   │   │   ├── mainUI.form
│   │   │   │   └── mainUI.java
│   │   │   ├── otherResources
│   │   │   │   ├── Algorithm.java               - the algorithm that assigns elevators to incoming jobs and coordinates stoppint at appropriate floors
│   │   │   │   ├── Constants.java               - shared constants
│   │   │   │   ├── InputFileMaker.java          - input file generator
│   │   │   │   ├── Message.java                 - message data structure
│   │   │   │   ├── MessageType.java             - enumeration class, determines the message type
│   │   │   │   └── MovingDirection.java         - enum class, determines the moving direction of an elevator
│   │   │   ├── schedStateMachine
│   │   │   │   ├── CheckAndHandleMessage.java   - main scheduler state
│   │   │   │   └── SchedulerState.java
│   │   │   └── udp
│   │   │       ├── UDP.java                     - udp connection class
│   │   │       └── UDPMail.java                 - udp connection class for the mailbox
│   │   └── resources
│   │       ├── Icons                            - image files for the gui
│   │       │   ├── bell_icon.png
│   │       │   ├── bell_icon_ring.png
│   │       │   ├── default.jpg
│   │       │   ├── elevator_icon.png
│   │       │   ├── elevator_icon_base.png
│   │       │   ├── elevator_icon_doorOpen.png
│   │       │   ├── elevator_icon_down.png
│   │       │   ├── elevator_icon_lampOn.png
│   │       │   ├── elevator_icon_opening.png
│   │       │   ├── elevator_icon_outOfOrder.png
│   │       │   ├── elevator_icon_up.png
│   │       │   ├── floor_buttons.png
│   │       │   ├── floor_buttons_down_pressed.png
│   │       │   ├── floor_buttons_up&down_pressed.png
│   │       │   ├── floor_buttons_up_pressed.png
│   │       │   ├── floor_first_button.png
│   │       │   ├── floor_first_up_pressed.png
│   │       │   ├── floor_top_button.png
│   │       │   └── floor_top_button_pressed.png
│   │       ├── input.txt
│   │       ├── input1Line.txt
│   │       ├── input2Lines.txt
│   │       ├── inputCapacity.txt
│   │       ├── inputFailsFloor22.txt
│   │       ├── inputFinal.csv
│   │       ├── inputFinal.txt              - test input file
│   │       └── inputMergedRequests.txt
│   └── test
│       ├── java
│       │   ├── ElevatorStateTests.java
│       │   ├── ElevatorTests.java
│       │   ├── FloorTests.java
│       │   ├── IntegrationTests.java      - integration tests
│       │   ├── MailboxTests.java
│       │   ├── MessageTests.java
│       │   ├── SchedulerStateTests.java
│       │   ├── TestSuite.java             - test suite that runs all the test files
│       │   └── UDPTests.java
│       └── resources
│           ├── inputMessageSendOnePassSingleFloor.txt  - input files for unit tests
│           ├── multipleFloors.txt
│           └── testinput1Line.txt

## Build and run

This project requires IntelliJ IDEA IDE to build and run. For more information about IntelliJ and download instructions, see https://www.jetbrains.com/idea/
    1. Open the ElevatorSimulator directory as a project in Intellij and download any necessary libraries, sdk, etc. when prompted by the IDE
    2. In IntelliJ, open the following files:
      a. src/main/java/allSystems/Mailbox.java
      b. src/main/java/allSystems/Scheduler.java
      c. src/main/java/allSystems/Elevator.java
      d. src/main/java/allSystems/Floor.java

    3. Run main() for each of the above files. IMPORTANT! they must be started in this order
                Mailbox, Scheduler, Elevator, Floor
    4. There will be 4 separate output tabs in the console. They can be dragged into 4 separate windows so output
    can be observed side by side
    5. Changing the input file:
        a. add the new file to the ElevatorSimulator/src/main/resources folder
        b. change the value of the constant FILE_PATH in ElevatorSimulator/src/main/java/otherResources/Constants.java to the new file name
                    e.g. public static final String FILE_PATH = "/myNewFile.txt";
        c. IMPORTANT! you must rebuild the project before starting to avoid ui errors
                    - in the top menu, go to Build->Rebuild Project

## Iteration 0
A real elevator was observed, measurements taken and statistics obtained regarding its average travel time, speed, acceleration, etc. The raw data and results are available in:
    - ProjectDocuments/Iteration0/SYSC3303Group1Iteration0Measurements.xlsx
    - ProjectDocuments/Iteration0/SYSC3003Group1Iteration0Report.pdf respectively

## Iteration 1
For this iteration, we have built the following functionality:
    - In this iteration we completed the following:
        - built the initial class structure of the project
        - created three threads: floor, elevator and scheduler
        - set up the mailbox framework in a thread safe data structure
        - passed one message between floor -> scheduler, scheduler -> floor, elevator -> scheduler, scheduler -> elevator
    - For this iteration, we only send the first message from the list that was read from the file as a proof of concept
    - The Floor Subsystem Simulator threads reads the scenarios from the input.txt files line by line, then saves them into a list
    - It then sends a Message to the Scheduler with the time, floor at which the request for service happened 
      and the direction the user would like to travel
    - Each message is tagged with metadata that shows the source and the intended recipient
    - The message will then be passed to and from the floor and the elevator threads going through the scheduler thread via
      an intermediary messaging class - Mailbox -which holds the messages for each recipient in a corresponding queue and implements 
      a producer - consumer pattern
    - After 1 pass, the threads are stopped for this iteration. In future iterations, the threads will run continuously until
      interrupted by turning off the application

## Iteration 2
This iteration introduced a state class for the elevator and for the scheduler. In addition, we added different message
types that will help handle incoming messages.

## Iteration 3
This iteration splits the system into 4 separate systems that run independently and communicate through UDP: 
Mailbox, Scheduler, Elevator, Floor.
- the Mailbox holds messages for the other 3 subsystems.
- the Floors read all the input lines from file, convert them into Messages and send them at appropriate time intervals
according to the timestamp in the string; each floor sends their own message (e.g. 4 down 2 is sent by floor 4)
- the floors also assign a uid to each message
- the messages are received by the Mailbox and placed in a queue for the Scheduler
- the scheduler retrieves each message and processes it according to its type in CheckAndHandleMessages
- the scheduler assigns an elevator to fulfil each job according to algorithm to optimize operation
- the algorithm will keep track of next floors for each elevator and add additional stops if they can pick up a passenger on the way
- every time the elevators pass a floor, they send a current floor update and the scheduler sends back a keep moving message or a stop message
- when an elevator stops, the scheduler tracks the jobs that are completed by that stop (either the starting floor or the destination floor)
- once a job is complete, the scheduler outputs the message together with its uid and sends a new job to that elevator

## Iteration 4
We are introducing fault handling.
There are 2 types of faults:
1. Hard fault - stuck in transit issue
    * the scheduler injects the fault by sending the elevator a fault message instead of a move message
    * instead of a successful current floor update, the elevator sends a fault message.
    * the scheduler handles this by sending back an out-of-order message
    * elevator goes into its out-of-order state.
    * scheduler handles the elevator’s current jobs:
        * changes the elevator’s current jobs that have starting floor fulfilled to have starting floor = to the current floor and sends them out to be reassigned (irl the passengers that are already in the elevator get out and call another elevator at the floor where they are)
        * sends the elevator’s other jobs that don’t have the starting floor fulfilled unaltered; these will also be reassigned to another elevator

2. Transient fault - door issue:
    * the scheduler injects the fault by sending the elevator a door fault message instead of a door close or door open message
    * the elevator sends a door fault message instead of a confirm door closed or confirm door opened message
    * the scheduler resends a second door close/open message to elevator and a second request to confirm door closed message
    * the elevator closes/opens door the second time successfully and sends confirm door closed message
    * the scheduler then sends move message as usual

Example file input lines:
timestamp	    startingFloor	    direction	    destFloor	    fault
13:51:37.29     1                       up               3             0
13:51:37.29     1                       up               3             1
13:51:37.29     1                       up               3             2

Diagrams included for Iteration 4:
    FaultHandlingSequenceIteration4.pdf     - sequence diagram for fault handling
    StateMachineUpdatedIter4.pdf            - updated state machine diagram
    TimingAveragesFaults.xlsx               - time stamps and calculated average over several runs of the program, used for the timing diagram
    UMLClassDetailed.png                    - detailed class diagram
    UMLClassDetailedWithDependencies.png    - detailed class diagram with dependencies
    UMLClassHighLevel.png                   - a high level view of the classes

## Iteration 5
This iteration introduces capacity limits and a graphical user interface. We also have instrumentation to record the time at which the first job is received by
the Scheduler and the times at which each job is finished.
A. Capacity limits
    - there are 5 people maximum allowed per elevator
    - the Scheduler will assign jobs only to those elevators that still have capacity, such that the maximum number of passengers per elevator is 5
    - if none of the elevators still have capacity, the Scheduler will wait a predetermined amount of time until trying to reschedule the job
    - the elevator loading/unloading time (the time that the door stays open) is 5 seconds per passenger that is loading/unloading at that floor
B. Graphical User Interface
    - this will pop up once the Scheduler is started
    - we are representing 4 elevator cars with 22 floors
    - each elevator has doors, a motor, buttons pressed inside the elevator, current floor and current number of passengers
    - each floor has an up arrow lamp and a down arrow lamp, with the exception of the first floor (only has up) and the last floor (only has down)
    - there is a log box where some of the output from the Scheduler can be observed, so that it can be reconciled with the actions in the ui
    - when an elevator is out of service, its motor at the top is marked in red

C. Instrumentation
    The system ran with the same input file for 10 attempts and the following times were recorded:
     - time at which the first job arrived in the Scheduler system, as sent from the Floor
     - time at which the last job completed
    The average time in which the system can complete the jobs in the input file is calculated in the attached spreadsheet.

Files specific to this iteration:
Input file: src/main/resources/inputFinal.txt
Spreadsheet for instrumentation: TimingInstrumentation.xlsx


## Test - Iteration 1
Manual testing:
    - run application as described in ## Build and run 
    - the floor subsystem class outputs the messages that were read from the input.txt file
    - observe output of the floor subsystem, elevator and scheduler threads as they pass a message
      back and forth
Run unit tests:
    - go to /test/TestSuite.java and click on the green arrow at the top right
    - test should pass if messages were sent floor -> scheduler, scheduler -> floor, elevator -> scheduler, scheduler -> elevator
    
## Test - Iteration 2
Manual testing:
	- run application as described in ## Build and run
	- the ElevatorState class outputs the states of the Elevator as the Elevator travels to floors

Unit Testing:
	- J-Unit tests from Iteration1 are maintained
	- Successfully fulfils 1 request (works for the first line of the input file)

Test Files Used:
	-input.txt
	-input1Line.txt
	-input2Lines.txt

## Test - Iteration 3
Manual testing:
1. Build and Run as above
2. Observe the Floors reading from file
3. The floors will send messages according to the timestamp
4. The scheduler will receive each message from floor and then assign that job to an elevator
5. The elevator receives commands from the scheduler and outputs the actions
6. The received messages by entity can be seen in the Mailbox window as they are received
7. Once a job is complete, the scheduler will output that job as complete
8. Compare the completed jobs in the scheduler with the jobs sent by the floors and with the elevator movements as it fulfils those jobs

## Test - Iteration 4
Automated testing: run all tests in one shot with ElevatorSimulator/src/test/java/TestSuite.java

Manual testing for fault handling:
We have ElevatorSimulator/src/main/resources/input.txt as our input file. The lines in the file will correspond to
the job uid, starting with index 0. E.g.line 1 in the file corresponds to job with uid=0
This file has 10 jobs in total, 1 of which has a hard fault (job uid=6, line 7 in the file) and 1 soft fault (job uid=4, line 5 in the file)
For the Elevator and the Scheduler system, the current time of the system is outputted for most print statements. This shows the time at
which certain events have taken place.

Prerequisite: Build and Run as above

A. Soft fault:
1. In the Scheduler window, observe 1 soft fault (door fault) being injected for job with uid=4
            e.g. Injecting soft fault to Elevator1 at floor 2 for job: uid=4
2. In the elevator window, observe the elevator that had job with uid=4 will have 1 door opening/closing fault
            e.g. Elevator1 failed to open doors! Job uid=4
3. The scheduler will get a message from the elevator that reports the fault
            e.g.: Elevator1 has a door opening fault at floor 2 job uid=4
4. The scheduler will send another door open/close message to that elevator
            e.g.: Sending Elevator1 a new OPEN_DOOR command...
5. In the elevator window, the elevator will try to open doors again
           e.g.: Elevator1 is opening doors...
6. In the scheduler window, the scheduler will get confirmation that the elevator successfully opened the door
            e.g.: Elevator1 opened the door job: uid=4

B. Hard fault:
1. In the Scheduler window, observe 1 hard fault being injected for job with uid=6
            e.g.: Injecting hard fault to Elevator2 at floor 2 for job: uid=6 13:51:56 1 up 3 fault=2
2. In the elevator window, observe the elevator that had job with uid=6 will get stuck
            e.g. Elevator 2 is stuck at floor 2
3. In the scheduler window, the scheduler will get a message from the elevator
            e.g. Elevator2 REPORT_STUCK at floor 2
4. The scheduler will take that elevator out of service and reassign its jobs
            e.g. Taking Elevator2 out of service, will modify and reassign the following jobs:
      Note: the jobs will be modified such that, if the starting floor was already visited, the new starting floor will be
      the current floor at which this elevator got stuck; the status will say NOT_STARTED because this is the new job we are sending out.
      The fault is also reset for the job if it was the one that had a fault.
5. The elevator with the fault will go into out of order state
            e.g. Elevator 2 is out of order
6. The scheduler will receive the jobs that need to be reassigned and assign them to a different elevator
            e.g. Assigned job: uid=6 13:51:56 2 up 3 fault=0 NOT_STARTED to Elevator1
7. No further jobs will be assigned to the elevator that is out of order


## Test - Iteration 5
Manual testing:
Test file used is: ElevatorSimulator/src/main/resources/inputFinal.txt
IMPORTANT: when switching input files, rebuild project Build->Rebuild Project to avoid UI errors
Capacity limits: the time that the door stays opened can be observed in the ui and in the output:
                e.g. Elevator2 keeping door open for 5 seconds at floor 13, loading/unloading 1 passengers...
                e.g. Elevator2 keeping door open for 15 seconds at floor 22, loading/unloading 3 passengers...
Towards the end of our input file, there are jobs put in meant to overload the elevators and trigger capacity limits.
Observe the jobs being split between different elevators

Automated testing:
There are unit tests for each class and integration tests that exercise various scenarios. All the tests
can be run as a test suite:
                        ElevatorSimulator/src/test/java/TestSuite.java
IMPORTANT: before running the test suite, please exit any running IntelliJ programs e.g. Scheduler, Mailbox, Floor or Elevator
