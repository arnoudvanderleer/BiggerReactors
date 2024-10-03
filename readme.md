# Evolutionary algorithms for bigger reactors
This employs an evolutionary algorithm to find the best bigger reactor.

## Running the project

1. Clone the repository, including submodules
    ```
    $ git clone --recurse-submodules -j8 https://github.com/arnoudvanderleer/BiggerReactors.git
    ```
2. Navigate to [`Simulation.java`](src/main/java/com/tempestasludi/p41_reactors/Simulation.java)
3. **Optional**: Tweak the settings for the evolutionary algorithm, at the top of the class definition.
4. Run `Simulation.java`. In VScode, you can do this by clicking on the "Run and Debug" icon in the bar on the left. In the panel that appears, click on the dropdown menu, and choose the option "Simulation" (from the options "Current file", "Simulation" and "Test").
5. Tell us about your findings!

Tested with the following setup (but will probably work with a wide range of setups):
| Software | Version |
|--|--|
| OS | Ubuntu 24.04 |
| Java | Openjdk 17.0.12 |
| IDE | Visual Studio Code 1.89.0 |
| Extensions | [Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug) |

## File layout
* In `src/main/java/net/roguelogix/multiblocks/reactor/simulation`, you will find the files from the mod that define the nuclear reactor simulation.
* In `src/main/java/com/tempestasludi/p41_reactors/` you will find the files that run this simulation with different configurations, to find the best possible nuclear reactor:
    * `Test.java` is a stub, which initializes one specimen and then evaluates its performance. If you quickly want to test something, you can use this (or create a new file).
    * `Algorithm.java` contains the evolutionary algorithm.
    * `Specimen.java` describes one specimen in the population. It keeps track of the genome (`moderators`), runs the simulation (`evaluate`) and can mutate or do crossover.
    * `Score.java` is a record class that keeps track of the score of a specimen. It contains the power yield, the fuel consumption and the fuel efficiency (yield / consumption) at the moment of greatest yield in the first 30 seconds of simulation. Note that `Algorithm.java` contains a function to compare two such scores.