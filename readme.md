# Evolutionary algorithms for bigger reactors
This employs an evolutionary algorithm to find the best bigger reactor.

## Running the project

1. Clone the repository, including submodules
    ```
    $ git clone --recurse-submodules -j8 https://github.com/arnoudvanderleer/BiggerReactors.git
    ```
2. Navigate to [`Algorithm.java`](src/main/java/com/tempestasludi/p41_reactors/Algorithm.java)
3. **Optional**: Tweak the settings for the evolutionary algorithm, at the top of the class definition.
4. Run `Algorithm.java`. In VScode, you can do this by clicking on the "Run and Debug" icon in the bar on the left. In the panel that appears, click on the dropdown menu, and choose the option "Algorithm" (from the options "Current file", "Algorithm" and "Test").
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
    * [`ReactorModeratorRegistry.java`](src/main/java/net/roguelogix/biggerreactors/registries/ReactorModeratorRegistry.java) contains a variable `registry` with the current moderator types that are used in the algorithm. You can add or remove moderators there, but don't forget to add or remove the corresponding colors in the `colors` array, because these are used to color the printed blocks.
* In `src/main/java/com/tempestasludi/p41_reactors/` you will find the files that run this simulation with different configurations, to find the best possible nuclear reactor:
    * `Test.java` is a stub, which initializes one specimen and then evaluates its performance. If you quickly want to test something, you can use this (or create a new file).
    * `Algorithm.java` contains the evolutionary algorithm.
    * `Specimen.java` describes one specimen in the population. It keeps track of the genome (`moderators`), runs the simulation (`evaluate`) and can mutate or do crossover. Its constructor `Specimen(Random random)` initializes a specimen, and you can either let it fill the initial reactor with a pattern of moderators (or just one moderator) and fuel rods, or make it select moderators and fuel rods at random.
    * `Score.java` is a record class that keeps track of the score of a specimen. It contains the power yield, the fuel consumption and the fuel efficiency (yield / consumption) at the moment of greatest yield in the first 30 seconds of simulation. Note that `Algorithm.java` contains a function to compare two such scores.

Note that the current setup of `Specimen` is heavily geared towards optimizing reactors with odd width and length, with just 1 fuel rod. You can of course update the code to
 * Initialize with more rods
 * Allow mutation to add or remove rods (currently, mutation can only change a non-rod block into another non-rod block)
 * Update the `positions` array likewise. This static array contains all the block positions that can contribute to the value of the reactor, because the rest will never receive radiation. In the setup with one fuel rod, most of these blocks are in a sphere with radius 5 around the bottom of the fuel rod. If you want to optimize reactors with multiple rods in the center 3x3 columns, for example, you will have to take the union of all the block positions that can receive radiation from the bottom 9 blocks of either of those columns. You can, of course, estimate this as the union of the cylinders with radius 5 around all the fuel rods. Or even simpler, just put every block in the reactor in the `positions` array. This will just make the algorithm slightly slower, because it will try to find optimal values for blocks that do not matter, but apart from that it is fine.