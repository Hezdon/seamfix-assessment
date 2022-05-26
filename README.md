# Read Me First
This is a Drone/Dispatch service that is used to dispatch medication to different location. Drones have their respective maximum weight they can carry, and a battery capacity which reduces once the drone is at work. 

# Getting Started
The application was compiled with JDK 11. In order to run the jar, simply download the [zip file](https://github.com/Hezdon/drone/blob/chidalu.egeonu-dev/drone-0.0.1-SNAPSHOT.zip) and run the command below and you are good to go - just make sure you have Java
running on your system. The application runs on port 9091 but of course you can change the port in the ***application.properties*** file in the resources folder. 
 ```
java -jar drone-0.0.1-SNAPSHOT.jar
```
**Once the app starts running, import the postman collection Drone.postman_collection.json and consume the endpoints provided. The service is using h2 database and can access via http://localhost:9091/h2-console, username is sa and emtpy password**.

