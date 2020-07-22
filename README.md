# Environment Check (Java)

This is an environment check for a Circle Internet Financial interview problem in Java.  You have probably been given this folder in advance of an interview with Circle to make sure that the actual interview problem will run on your development machine during the interview.


## Checking your environment

Make sure you have the following prerequisites installed:
  - [Java 8 JDK & JRE](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
  - [Maven](https://maven.apache.org/install.html)
  - [Docker](https://docs.docker.com/install/)

Make sure you do not have anything running on ports 5432 or 8080.  Once you have done so, start the database with:
```bash
$ ./start_db.sh
```

Then, either use your IDE to build and run the program using main class `ServiceApplication.java`, or build and run it in a new terminal window:
```bash
$ mvn install
$ java -jar "./target/platform-pair-envcheck-0.0.1-SNAPSHOT.jar" server configuration.yml
```

Finally, in a third window, make sure you can curl the Dropwizard server.  You should receive something like the following (adjusted to the current time):
```bash
$ curl localhost:8080
{"data":"2020-03-16T02:20:37.256Z"}
```

If you receive something like the above result, then you're all ready for the interview!  If you receive an error, or no response, check the window with the Dropwizard server for logs that may help you debug the issue (not starting the database, or an old version of Java, are the most common).  If you continue having problems, email whomever sent you this package describing your error and they'll help you get it sorted out.

Good luck on your interview!
