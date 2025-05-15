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
└── src
    ├── main
    │   ├── java
    │   │   ├── InputFileMaker.java
    │   │   └── primary
    │   │       ├── Elevator.java                        - elevator thread class
    │   │       ├── ElevatorState.java                   - elevator state class
    │   │       ├── Floor.java                           - floor thread class
    │   │       ├── Mailbox.java                         - thread-safe singleton class that receives messages, holds them in separate 
    │   │       │              								queues (1 per thread) according to recipient, and releases them to the recipient
    │   │       ├── Message.java                         - message that corresponds to 1 line of input from the input.txt file
    │   │       ├── MessageType.java                     - types of messages that will be sent; used when handling different messages
    │   │       ├── MovingDirection.java                 - types of directions; used for handling different cases
    │   │       ├── Scheduler.java                       - scheduler thread class
    │   │       ├── SchedulerState.java                  - scheduler state class
    │   │       └── main.java                            - main class
    │   └── resources
    │       ├── input.txt                                - input file for the application with multiple requests
    │       └── inputOneLine.txt                         - input file for the application with one request
    └── test
        ├── java
        │   └── ECSTest.java                             - JUnit test class
        └── resources
            ├── inputMessageSendOnePassSingleFloor.txt   - text files containing one request
            └── inputReadFromFileMultipleFloors.txt      - text file containing multiple requests

## Build and run

This project requires IntelliJ IDEA IDE to build and run. For more information about IntelliJ and download instructions, see https://www.jetbrains.com/idea/

    - Open project in Intellij and download any necessary libraries, sdk, etc. when prompted by the IDE
    - In IntelliJ, open ElevatorSimulator as a project
    - select on the main.java file
    - Click the green arrow at the top right; this will build and run the project
    - Output can be seen in the console

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