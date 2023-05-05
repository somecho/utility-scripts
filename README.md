# Somē's utility scripts

Here are some utility scripts I wrote for myself. For the most part, it's mainly to learn about scripting in shell but also to automate some repetitive tasks. These scripts were written on a Linux system and are expected to work on Linux systems.

## Scripts
### [create-clj-minimal](./create-clj-minimal)
A script to create an ultraminimal clj project with an empty deps.edn and a singular hello world main function. To use, simply call `create-clj-minimal my-minimal-clj-project` and a project called `my-minimal-clj-project` will be created for you. Mainly used for quick hacking and throwaway prototyping.
### [stoptasks](./stoptasks)
Stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks. Every tried `task stop` and gotten an error? Yeah, me too. Now you can stop all active tasks with a single `stoptasks`.
## Usage suggestion
You could copy the scripts to `/usr/local/bin` or to any other directories that are in your `PATH`. It will then be globally accessible.
## Change log
- [584d3d0](https://github.com/somecho/utility-scripts/commit/584d3d04b3d9d2a9d1fdd79789e7c4908daa40be) - added [create-clj-minimal](./create-clj-minimal)
- [4b8c492](https://github.com/somecho/utility-scripts/commit/4b8c492ecd1725646dbff502a19a77cc73c52747) - added [stoptasks](./stoptasks)
