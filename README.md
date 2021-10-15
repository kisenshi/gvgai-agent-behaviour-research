# Agent Behaviour Research (in GVGAI)

### Automated Gameplay branch

_This branch contains the code related to the standalone created for to execute automated gameplay of a game in GVGAI using a particular agent description._

--------

<br/>

Personalised version of the GVGAI framework that allows providing the heuristic to use in the algorithm at the time of starting the game. This modification allows running the agents with different heuristics (behaviours) without having to update their core code. The heuristics are created in a external file and plugged into the agent during its instantiation.  

This repository contains the code related to my research in agent behaviour. it covers the following topics:

- [Heuristic diversification](#heuristic-diversification)
- [Use of MAP-Elites to generate agents that elicits diverse automated gameplay](#map-elites-to-generate-a-team-of-agents-with-diverse-behaviours)
- [Automated gameplay](#automated-gameplay) (this branch)

## Original code: GVGAI competition

The original code of the GVGAI Framework can be found at https://github.com/GAIGResearch/GVGAI. It has been duplicated in this repository and included some modifications to fit the needs of my experiments. Please refer to the original code for anything related to the GVGAI competition.

The code was duplicated on the __13th of January of 2021__ so this repository does not include any modification/fix carried out in the main repository after this date.

## Behaviour research code

The code related to the behaviour research is contained on its own folder: _src/heuristic_diversification/_

## Heuristic diversification

The interests of [my research](http://kisenshi.github.io/research/) is widening the use of existing algorithms when these are provided with goals that go beyond winning. The modifications included in this repository allow providing different heuristics to a same algorithm without having to modify its core, which was essential for my experiments.

The modifications are similar to the ones carried out in previous experiments but they have been integrated in the most recent version of GVGAI, cleaned, improved and polished.

### Core modifications

1) Disable TIME_CONSTRAINED set for the competittion

2) Create new classes to provide the heuristic and the controller separately when running a game.

    - _src/core/heuristic/StateHeuristics.java_
    - _src/core/player/AbstractHeuristicPlayer.java_

3) Create new file to replace _ArcadeMachine_ so the methdos related to the heuristic diversification are in their own file

    - _src/heuristic_diversification/helper/ArcadeMachineHeuristic.java_

4) Modify sample controllers (OSLA and MCTS) to support heuristic diversification.

    - _src/heuristic_diversification/controllers/_

5) Folder to contain the files with heuristics and helper classes

    - _src/heuristic_diversification/heuristics/_

6) Create GameStats class to collect final stats of the gameplay

    - _src/heurstic_diversification/model/GameStats.java_

7) Create test main to make sure the heuristic diversification works

    - _src/heuristic_diversification/testDemo.java_

### Related paper

Guerrero-Romero, Cristina, Annie Louis, and Diego Perez-Liebana. "[Beyond Playing to Win: Diversifying Heuristics for GVGAI](http://kisenshi.github.io/files/201708_PlayingDiversifying.pdf)." _In 2017 IEEE Conference on Computational Intelligence and Games (CIG)_, pp. 118-125. IEEE, 2017.

__Note__: For the code and results obtained in the experiments presented in the paper, refer to the [original repository](https://github.com/kisenshi/gvgai-experiments).

## List of heuristics (player behaviours) implemented

The heuristic implemented are general within the GVGAI framework. They can be
used in any of the games supported by it, as long as the rules are well-formed and
aligned with the assumptions made in each of the heuristics (detailed in the paper
"[MAP-Elites to Generate a Team of Agents that Elicits Diverse Automated Gameplay](http://kisenshi.github.io/files/paper-map-elites-generation-team-agents-behaviour.pdf)").

- **Winning and Score**: Winning  the  game while maximizing the score difference.
- **Exploration**: Maximizes the physical exploration of the map prioritizing those positions that haven’t been visited before or have been visited the least number of times.
- **Curiosity**: Maximizes the discovery and interaction with sprites in the game,  prioritizing interactions with new sprites. Interactions are collisions between the avatar (or a sprite created by the avatar) and the elements of the game.
- **Killing**: Maximizes the destruction of Non-Playable Characters (NPCs).
- **Collection**: Maximizes the collection of resources available in the game.

A _parent_ heuristic called _TeamBehaviourHeuristic_ has also been implemented. It can be assigned to the agent to set different combinations of the previous behaviours, by assigning a _weight_ to each of them. It's been necessary to include normalisation logic to the heuristics so their results are on a similar scale.

## MAP-Elites to generate a team of agents with diverse behaviours

Implementation of the MAP-Elites algorithm to generate a team of agents that play a game eliciting different types of gameplay. Each of the cells of the map contains the description of an agent that implements the _TeamBehaviourHeuristic_ given by a set of weights evolved by a stochastic hill climber mutation. 

The candidates are assigned to the map by looking at the stats resulting from playing the game repeatedly. The performance used to replace elites in the map is not related to how well the agent performs in the game in terms of wins or score, as these are considered features, but by how fast the agents end up getting a pair of features in a similar range.

### Contents

- _src/heuristic_diversification/mapelites_ Contains _MAPElites.java_ and the necessary classes and helpers to generate the map of elites. The features come from the resulting stats of playing a game, calculated by GameStats when playing the game.

- _src/heuristic_diversification/framework/_ Contains the classes related to the GVGAI framework so the MAP-Elites algorithm is as transparent to it as possible.

- Mutation: _src/heuristic_diversification/mapelites/Generator.java_ contains the methods that generate random values and mutate the weights given to the agents. We use a stochastic hill climber mutation to get new values for the weights.

- _src/heuristic_diversification/model/JSONManager.java_ Class with static methods to serialize the objects and generate the JSON files containing the results of the experiments (and read from them when necessary).

- _src/heuristic_diversification/config/_ Contains information about the agents, behaviours and games available for the experiments.

### Main

- _src/heuristic_diversification/MapElitesGameplay.java_ Contains the code used in the experiments. Reads the configuration files, sets up and prepares the agent, initializes the MAP-Elites and runs it based on the configuration provided. Creates logs and generates the final JSON with the results.

- _src/heuristic_diversification/GenerateMapElitesGameplayConfigTemplate.java_ Generates a template to create valid configuration files.

- _src/heuristic_diversification/mapElitesTest.java_ Test file used during development. Can be ignored.

### Experiments

- The games and levels used in the experiments can be found in their own separate folder: _examples/experiments/_

The config files, executables and results of the experiments carried out have been uploaded and can be found in an [OSF repository](https://osf.io/whxm8/). The scripts to process and parse the resulting JSON files to generate the heatmap graphs, record the videos, etc are in [another Github repository](https://github.com/kisenshi/experiments-automated-gameplay-results-processing).

### Demo

I have implemented an interactive tool that allows to visualize the gameplays of the agents generated by running the experiments. It allows checking the details and stats of the agents generated in each experiment, playing a pre­-recorded gameplay of each of them and to download the standalone and instructions to running them locally to witness their behaviour first hand.

The link to the live demo is: https://demo-visualize-diverse-gameplay-xqjmp.ondigitalocean.app/

### Publication

Guerrero-Romero, Cristina, and Diego Perez-Liebana. "[MAP-Elites to Generate a Team of Agents that Elicits Diverse Automated Gameplay](http://kisenshi.github.io/files/paper-map-elites-generation-team-agents-behaviour.pdf)" To be published _in 2021 IEEE Conference on Games (CoG)_

### Related paper

Guerrero-Romero, Cristina, Simon M. Lucas, and Diego Perez-Liebana. "[Using a Team of General AI Algorithms to Assist Game Design and Testing](http://kisenshi.github.io/files/paper-team-general-ai-assist.pdf)." _In 2018 IEEE Conference on Computational Intelligence and Games (CIG)_, pp. 1-8. IEEE, 2018

## Automated gameplay

I have implemented an executable that allows triggering an automated gameplay of a 
game of the GVGAI framework by providing a config file with the details of the game, agent and the description of its behaviours. 

- The folder containing the files and downloadable zip can be found at localAutomatedGameplay/.

- _src/heuristic_diversification/AutomatedGameplay.java_ contains the code of the main that has been used to generate the jar included in the standalone.

## License

The original code and License of the GVGAI Framework can be found at: 
https://github.com/GAIGResearch/GVGAI

The original code has been extended to fit the needs of my research. All the modifications and extensions can be found in this repository, created by Cristina Guerrero-Romero in 2021.
 
Copyright (C) 2021 Cristina Guerrero-Romero

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
