Risk
====

Java code repository for the Risk artificial intelligence project done by Dirk Brand for a Bsc Honours in Computer Science degree at the University of Stellenbosch.  The computer players used for the research component (both the expectiminimax and Monte Carlo tree search players), are also included.  For information on the results of the research, please contact me directly.

This project is completely open source and any further work on it would be greatly appreciated.  Collaboration is encouraged!

If anyone would like to sponsor some Bitcoin for further projects in Monte Carlo tree search work, please send to the following address: 14QrgzjVVVuewsR7DCWMtpU9ZXeDqokmCH

GETTING STARTED:

*All instructions here assume you are running the commands in the root of the project.*

This project is built using Gradle.  If you have gradle installed (or install gradle on your system):
-you can run "gradle eclipse" to set up the project for use in Eclipse (including resolving dependencies), after which you can import it as an existing project into Eclipse.
-otherwise, you can work on the command line, with "gradle build"
-for more options, run "gradle help"

If you don't or can't have gradle installed system-wide, this project comes with a gradle wrapper that installs and used a local version of gradle in your user account.  To
use this just run "./gradlew" instead of gradle in the instructions above.

For developers who wish to develop in Eclipse: this has not been tested, but [here is a potentially useful link for better integration of Gradle and Eclipse](https://github.com/spring-projects/eclipse-integration-gradle).

USAGE:

*More details on which classes to run can be found in build.gradle*

Launch the facilitator that waits for connections.
Launch the client application(s) that connect to the server.

