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
ElevatorSimulator
├── README.txt
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── allSystems
│   │   │   │   ├── Elevator.java                        - elevator threads class, runs independently with its own main(),
                │                                           sends and receives messages from Mailbox
│   │   │   │   ├── Floor.java                           - floor threads class, runs independently with its own main(),
                │                                           sends and receives messages from Mailbox
│   │   │   │   ├── Mailbox.java                         - class that receives messages, puts them in separate 
    │   │       │              				               queues (1 per thread) according to recipient, and releases them to the recipient
│   │   │   │   └── Scheduler.java                       - scheduler thread class, runs independently with its own main(),
                │                                           sends and receives messages from Mailbox
│   │   │   ├── elevStateMachine                         -  the different states for the elevators as they cycle through 
│   │   │   │   ├── DoorClosing.java                     
│   │   │   │   ├── DoorOpened.java
│   │   │   │   ├── DoorOpening.java
│   │   │   │   ├── ElevatorState.java
│   │   │   │   ├── Idle.java
│   │   │   │   ├── Moving.java
│   │   │   │   ├── Stopped.java
│   │   │   │   └── WaitingToMove.java
│   │   │   ├── otherResources
│   │   │   │   ├── Algorithm.java                       - the algorithm that assigns elevators to incoming jobs and coordinates stoppint at appropriate floors
│   │   │   │   ├── Constants.java                       - shared constants
│   │   │   │   ├── InputFileMaker.java                  - input file generator
│   │   │   │   ├── Message.java                         - message data structure
│   │   │   │   ├── MessageType.java                     - enumeration class, determines the message type
│   │   │   │   └── MovingDirection.java                 - enum class, determines the moving direction of an elevator
│   │   │   ├── schedStateMachine
│   │   │   │   ├── CheckAndHandleMessage.java           - main scheduler state
│   │   │   │   └── SchedulerState.java
│   │   │   └── udp
│   │   │       ├── UDP.java                             - udp connection class
│   │   │       └── UDPMail.java                         - udp connection class for the mailbox
│   │   └── resources                                    - input files for main program
│   │       ├── input.txt
│   │       ├── input1Line.txt
│   │       ├── input2Lines.txt
│   │       └── inputMergedRequests.txt
│   └── test
│       ├── java
│       │   └── ECSTest.java                            - test class
│       └── resources                                   - input files for tests
│           ├── inputMessageSendOnePassSingleFloor.txt  
│           └── inputReadFromFileMultipleFloors.txt
└

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

## Test
    - The Floor Subsystem Simulator reads the information from the input.txt file. 
    Unit tests are included. To run, go to /test/ECSTest.java and click on the green arrow at the top right
    Note: this is only for iteration 1

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

## Test - Iteration 1
Manual testing:
    - run application as described in ## Build and run 
    - the floor subsystem class outputs the messages that were read from the input.txt file
    - observe output of the floor subsystem, elevator and scheduler threads as they pass a message
      back and forth
Run unit test:
    - go to /test/ECSTest.java and click on the green arrow at the top right
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
