# Project Report | Group 6
## Presentation Link: [Here!](https://docs.google.com/presentation/d/1BzaEs5NAVcdYS_kA8tDKT4DYsPtknE7kZclVpFuIz_I/edit?usp=sharing)

## Steps to run code
- Download/Import the code to a respective Java-applicable IDE
- Make sure your IDE is using the latest Java Development Kit (JDK 21)
- Download and configure the following libraries into your IDE:
[JavaFX](https://gluonhq.com/products/javafx/), [slf4j-api-2.0.9](https://github.com/trungtran1234/CS151-CollaborativePlanner/releases/download/lib/slf4j-api-2.0.9.jar), & [sqlite-jdbc-3.44.0.0](https://github.com/trungtran1234/CS151-CollaborativePlanner/releases/download/lib%2B/sqlite-jdbc-3.44.0.0.jar)
- Run the Main.java class

## Snapshot of the running program
![image](https://github.com/trungtran1234/CS151-CollaborativePlanner/assets/131917314/d55230ed-5e56-402a-a72b-ef86a0af84bf)

## Brief description of how you solved the problem mentioned in the proposal
While our team originally proposed a collaborative planner application, we made the selective decision to cut down our project because the new technologies we would have to learn for a collaborative planner were out of scope for this class. Since we were all inexperienced, having to learn new advanced technologies would take up too much time for this project. And so we decided to settle for an individual planner application, which still contains the core concept of our original proposal. The problem of people needing help with time management and having to coordinate their schedules has still been resolved within our application; it sufficiently allows users to create and organize their schedules through the use of our application.

## UML Class Diagram
![umlclass](https://github.com/trungtran1234/CS151-CollaborativePlanner/assets/131917314/e4455f95-12fe-4562-aa63-39a0896fcb6a)

## Brief description of your application
- Our application is an individual planner that allows users to create their own custom schedules in a planner. Available actions include setting schedule blocks by time with a description, overlapping events, a color picker for the blocks, a dynamic weekly planner viewer, and it is also in sync with your current time.

# Original Project Proposal: Collaborative Planner
## Group 6
## By Trung Tran, Edison Nguyen, and Phuc Nguyen

### The problem to resolve:
With everyone having their own busy schedules these days, it’s hard for groups to coordinate and align their plans effectively. As groups come together to plan for meetings and deadlines, it can be difficult to assess everyone’s mutual availability. This problem is relevant as what timeslot is available for one individual may not be available for others.
 
### Functionality: 
Our team would like to propose a solution to the problem by creating a collaborative planner. The application will let groups of users share a planner and collaborate on scheduling, making sure everyone is on the same page. As the application takes in users' schedules, it will provide an output of the available time that everyone shares in order to find the best possible time to collaborate efficiently.	

### Previous works
[1] Timely - Subscription-based team time-management application. <br>
[2] Resource Guru - Subscription-based planner application that puts all group members' schedules on one screen.

### Assumptions / operating environments / intended usage 
Our collaborative planner is intended to be used by groups of individuals who want to coordinate their activities based on each other’s schedules. This application can be used in various industries where efficient group planning is needed. For example, it can be used by students, friends, family, business partners, and project team members.

### High-level description of solution
#### Approach:
We plan to make a desktop-based collaborative planner application. We plan to follow the Waterfall Model for developing this application. There will be discussions for when to move on to the next step based on the progress of the project. It is expected that we will meet every week to discuss what we did in the past week and what we plan for the next week. Meeting times will vary depending on everyone’s availability. We will communicate through Discord and help each other out so that we can learn and complete this project together.

#### Final approach(updated): Due to limited time and resources, we were not able to deliver a completed product as planned, however, we were able to make an individual planner that essentially helps the user manage their time better by inputting schedule blocks in their planner from start time to end time and they are also able to write a description of their event and choose the schedule block color. Moreover, the schedule can be extended from week to week week the actual dates in the calendar.

#### Key features (updated):
**Account management** - Users will be able to register, log in, log out, and edit a personal account. (No longer needed because it is an individual schedule ) <br>
**Group creation** - Users will be able to create groups and invite others to the group to start collaborative planning. (No longer needed because it is an individual schedule )  <br>
**Shared calendar** - Let users see everyone’s schedule on the same calendar. Users will also be able to add or remove their own schedules to the calendar. (No longer needed because it is an individual schedule )  <br>
**Commenting** - Users can highlight a specific timeline on the calendar and write a comment or note. <br>
**Edit feature** - Users can now edit the time and description of their schedule block. <br>
**Provide availability timeslots** - The application will provide the group with mutual availability timeslots based on everyone’s schedule. Whenever a user makes an adjustment to their schedule, the new adjusted availability schedule will be generated to replace the previous one. (No longer needed because it is an individual schedule ) <be>


#### Technologies we will use:
Collaborative Platform: Github and Discord <br>
Programming Language: Java <br>
IDE: Eclipse <br>
Framework: JavaFX <br>
Database: MySQL (changed to SQLite) <br>

### References: 
[1] Timely, Timely AS (https://timelyapp.com/) <br>
[2] Resource Guru, Resource Guru (https://resourceguruapp.com/)

