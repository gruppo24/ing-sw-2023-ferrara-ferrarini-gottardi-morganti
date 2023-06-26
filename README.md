# Scaglione San Pietro - Gruppo 24

### Members of the group

* Ferrara Silvia
* Ferrarini Andrea
* Gottardi Arianna
* Morganti Tommaso

### Implementation details

* Full game rules
* GUI and CLI interfaces available
* jRMI and Socket server capabilities
* Multiple simultaneous games possible
* Client reconnections after disconnections
* Server-side game persistency / backups

### Running server & client JARs

To run the server and client jars simply run the following commands in a terminal:

```java -jar Server.jar``` & ```java -jar Client.jar```

*Note: the client JAR is a single entrypoint both for the GUI and CLI interface.*

### How to play in the GUI?

Whereas the *Command Line Interface* is rather self explanatory, using the GUI may
require a couple extra instructions. During a game, a player performs the following
actions when it is their turn:

1. Select a column by clicking it in the upper library.
2. Pick tiles from the board. After each pick, the chosen tiles will be moved below
the upper library.
3. Select the order with which the picked tiles should be inserted in the library.
Do so by clicking the picked tiles in the desired order, click twice to undo a selection.
