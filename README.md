# Somē's utility scripts

Here are some utility scripts I wrote for myself. At first I wanted to write the scripts in a shell scripting language. But then I discovered [Babashka](https://github.com/babashka/babashka) and I love Clojure. I decided to port all the scripts to Clojure instead. You will need [Babashka](https://github.com/babashka/babashka) to run these scripts.

## Scripts
### [cljminimal](./cljminimal.clj)
A script to create an ultraminimal clj project with an empty deps.edn and a singular hello world main function. To use, simply call `./cljminimal.clj my-minimal-clj-project` and a project called `my-minimal-clj-project` will be created for you. Mainly used for quick hacking and throwaway prototyping.
### [startnewtask](./startnewtask.clj)
Creates and immediately starts a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task. Use this as you would `task add`.
```sh
task add +admin +bookkeeping track finance # adds a task to Taskwarrior
./startnewtask.clj +admin +bookkeeping track finance # adds and starts task
```
### [stoptasks](./stoptasks.clj)
Stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks. Every tried `task stop` and gotten an error? Yeah, me too. Now you can stop all active tasks with a single `./stoptasks.clj`.
## Usage suggestion
You could copy the scripts to `/usr/local/bin` or to any other directories that are in your `PATH`. It will then be globally accessible.
## Change log
- [584d3d0](https://github.com/somecho/utility-scripts/commit/584d3d04b3d9d2a9d1fdd79789e7c4908daa40be) - added [create-clj-minimal](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) shell script
- [4b8c492](https://github.com/somecho/utility-scripts/commit/4b8c492ecd1725646dbff502a19a77cc73c52747) - added [stoptasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/stoptasks) shell script
- [41de9d4](https://github.com/somecho/utility-scripts/commit/41de9d4fd0103c7b1cefa4b47439054353a59a91) - added [starttasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) shell script
- [8bd623e](https://github.com/somecho/utility-scripts/commit/8bd623ef16068c4ed0ece1d1df32ed6bb0b210b8) - ported [create-clj-minimal](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) to Clojure. It is now called [cljminimal](./cljminimal.clj)
- [3a57c9a](https://github.com/somecho/utility-scripts/commit/3a57c9abac263ceea7add9513b70868862b98d1d) - ported [starttasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) to Clojure. It is now called [startnewtask](./startnewtask.clj)
- [e610b0b](https://github.com/somecho/utility-scripts/commit/e610b0b5c82580de74f6ccb644e9e092f9f7e130) - ported [stoptasks](./stoptasks.clj) to Clojure.
