Foraging Simulator Configurations
=================================

A simulation is configured using three different files: a simulator file, a patch file, and an agents file.  The simulator file indicates the patch and agent file to use.  All values, including the patch and agent files to use, can be overridden using command line options at startup.

Simulator Configuration
-----------------------
**__ToDo__**


Patch Configuration
-------------------

* `patch-count` The number of patches in the simulation
* `patch.##` Indicates the particular patch to which this key applies.  The value `##` is left padded with zeros permitting a total of 100 patches.
* `position` The position of the patch in 3-dimensional space with components separated by commas
* `resources` The amount of resources in the patch
* `radius` The radius of the patch denoting the area in which resources can be gathered
* `predation-probability` The probability per timestep of a predator capturing a prey within the bounds of the patch
* `min-agent-forage-count` The minimum number of agents that must be foraging for resources to be gathered

```
patch-count = 2
patch.00.position = 50,0,0
patch.00.resources = 50
patch.00.radius = 5
patch.00.predation-probability = 0.01
patch.00.min-agent-forage-count = 1
patch.01.position = 100,20,0
patch.01.resources = 50
patch.01.radius = 8
patch.01.predation-probability = 0.01
patch.01.min-agent-forage-count = 1
```

Agent Configuration
-------------------

* `agent-count` The number of agents in the simulation
* 'agent.###`  Indicates the particular agent to which this key applies.  The value `###` is left padded with zeros permitting a total of 1000 agents.
* `position` The position of the agent in 3-dimensional space with components separated by commas
* `team` The name of the team of which the agent is a member

```
agent-count = 2
agent.000.position = 0,0,0
agent.000.team = FlockingTeam
agent.001.position = 0,1,0
agent.001.team = FlockingTeam
```
