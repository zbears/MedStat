# MedStat
A wearable medical wristband

## Eagle Setup
In order to modify the cirucits in Eagle, clone the git repo then open Eagle. Go to Options->Directories and add the /circuits/libraries folder to your libraries path. Then add the /circuits folder to your projects path.

## Creating/Modifying Circuits
To create or modify a circuit, please create a separate branch from master. Go into the /circuits folder and create a directory whose name corresponds to the circuit you are creating. When you've finished creating the circuit, submit a pull request and have your circuit verified by one engineer. Once the circuit is verified, make the modifications to the main MedStat eagle project to include your circuit, submit another pull request to master, have one other engineer verify your circuit, then merge your branch with master. Do not delete your branch after merging with master.
