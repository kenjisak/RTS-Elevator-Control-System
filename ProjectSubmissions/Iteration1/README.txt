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
├── ElevatorSimulator
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   ├── ButtonPackage
│   │   │   │   │   ├── Button.java			
│   │   │   │   │   ├── CarFloorNumberButton.java	
│   │   │   │   │   ├── DoorInterruptButton.java	
│   │   │   │   │   └── FloorButton.java		
│   │   │   │   ├── ElevatorPackage
│   │   │   │   │   ├── ArrivalSensor.java		
│   │   │   │   │   ├── Door.java			
│   │   │   │   │   ├── Elevator.java			
│   │   │   │   │   └── Motor.java			
│   │   │   │   └── primary
│   │   │   │       ├── main.java			
│   │   │   │       ├── EntityType.java			
│   │   │   │       ├── Floor.java			
│   │   │   │       ├── FloorSubsystemSimulation.java
│   │   │   │       ├── Message.java			
│   │   │   │       ├── Mailbox.java			
│   │   │   │       └── Scheduler.java			
                  InputFileMaker.java
│   │   │   └── resources
│   │   │       └── input.txt	
│   │   └── test
│   │       └── java
│   │           └── ECSTest.java			
│   └── target
├── ProjectDocuments
│   ├── Iteration0
│   │   ├── SYSC3003Group1Iteration0Report.pdf          - Iteration 0 report document
│   │   └── SYSC3303Group1Iteration0Measurements.xlsx   - Iteration 0 elevator measurements - raw data 
└── README.txt

ElevatorSimulator
├── README.txt
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── ButtonPackage                   - parent class for all buttons
│   │   │   │   ├── Button.java
│   │   │   │   ├── CarFloorNumberButton.java   - class for the buttons inside the elevator that correspond to a destination floor
│   │   │   │   ├── DoorInterruptButton.java    - class for the buttons inside the elevator that interrupt the door opening or closing
│   │   │   │   └── FloorButton.java            - class for the buttons at each floor where a user can request an elevator
│   │   │   ├── ElevatorPackage
│   │   │   │   ├── ArrivalSensor.java          - sensor that will notify the scheduler when an elevator has arrived at a certain floor         
│   │   │   │   ├── Door.java                   - the door of the elevator
│   │   │   │   ├── Elevator.java               - elevator thread class
│   │   │   │   └── Motor.java                  - the motor of the elevator, controlled by scheduler
│   │   │   ├── InputFileMaker.java             - utility class that generates the input.txt file
│   │   │   └── primary
│   │   │       ├── EntityType.java             - enumeration type class that describes the different types of thread entities (floor, scheduler, elevator)
│   │   │       ├── Floor.java                  - floor thread class
│   │   │       ├── Mailbox.java                - thread-safe singleton class that receives messages, holds them in separate queues (1 per thread) according to recipient, and releases them to the recipient
│   │   │       ├── Message.java                - message that correspomds to 1 line of input from the input.txt file
│   │   │       ├── Scheduler.java              - scheduler thread class
│   │   │       └── main.java                   - main class
│   │   └── resources
│   │       └── input.txt                       - this is the input file for the application
│   └── test
│       └── java
│           └── ECSTest.java                    - JUnit test class
└──

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

## Test - Iteration 1
Manual testing:
    - run application as described in ## Build and run 
    - the floor subsystem class outputs the messages that were read from the input.txt file
    - observe output of the floor subsystem, elevator and scheduler threads as they pass a message
      back and forth
Run unit test:
    - go to /test/ECSTest.java and click on the green arrow at the top right
    - test should pass if messages were sent floor -> scheduler, scheduler -> floor, elevator -> scheduler, scheduler -> elevator
    
