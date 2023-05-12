# Somē's utility scripts

Here are some utility scripts I wrote for myself. At first I wrote the scripts in a shell scripting language. But then I discovered [Babashka](https://github.com/babashka/babashka) and I love Clojure. I decided to port all the scripts to Clojure instead. You will need [Babashka](https://github.com/babashka/babashka) to run these scripts. These are helper tools for [Clj](https://clojure.org/guides/deps_and_cli), Java, [Ledger](https://github.com/ledger/ledger) and [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior).

### [Scripts](#scripts) included:
1. [cljminimal](#cljminimal) - creates an ultra barebones deps.edn [clj](https://clojure.org/guides/deps_and_cli) project for quick hacking
2. [jrun](#jrun) - single file Java runner 
3. [keepbooks](#keepbooks) - simple transaction entry helper for [Ledger](https://github.com/ledger/ledger) CLI accounting
4. [on-modify-log](#on-modify-log) - a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) hook to log the latest modified task
5. [projectsof](#projectsof) - finds directories of certain project types
6. [resumetask](#resumetask) - resumes latest modified [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
7. [startnewtask](#startnewtask) - creates and starts a new [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
8. [stoptasks](#stoptasks) - stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks
9. [taskinfo](#taskinfo) - prints the attribute of a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
 
## Installation
You need to first [install Babashka](https://github.com/babashka/babashka#quickstart). 
 ```sh
 git clone https://github.com/somecho/utility-scripts
 cd utility-scripts
 ./install.clj 
 ```
 This will copy all the scripts into `~/.local/bin`. Make sure `~/.local/bin` is in your path to call the scripts globally.
 
### Uninstalling
 To uninstall, simply call `uninstall-some-scripts` and all the scripts will be deleted from `~/.local/bin`.
 
## Scripts
 
### [cljminimal](./cljminimal.clj)
A script to create an ultraminimal clj project with an empty deps.edn and a singular hello world main function. To use, simply call `cljminimal my-minimal-clj-project` and a project called `my-minimal-clj-project` will be created for you. Mainly used for quick hacking and throwaway prototyping.

### [jrun](./jrun.clj)
Compiles and runs a single java file. Mainly used for quick iteration of ideas. For example, you can run it in Vim with `:!jrun App.java` and see the output in a Vim buffer without leaving your current buffer.
#### Usage
```sh
jrun JAVAFILE
```
The `JAVAFILE` argument is glob-searched, so you can use `App.java` or `App` and it will still run.

### [keepbooks](./keepbooks.clj)
A helper script to enter a simple transaction into a [Ledger](https://github.com/ledger/ledger) file. The script has the following format:
```sh
keepbooks -f LEDGERFILE -d DATE PAYEE? ACCOUNT_TO_DEBIT ACCOUNT_TO_CREDIT AMOUNT CURRENCY
```
The `-d DATE` field is optional. If this flag is ommitted, the current date will be used. The `PAYEE` field is also optional. If the `PAYEE` is ommitted, no payee will be entered in the transaction. The other fields `ACCOUNT_TO_DEBIT`, `ACCOUNT_TO_CREDIT`, `AMOUNT` and `CURRENCY` are required fields. The ordering is strict. Upon entering a successful command, the ledger entry will be written into the ledger file provided and also printed out in the commandline.
```sh
keepbooks -f 2023.ledger -d 2023/07/20 Sushi Bar Expenses:Restaurant Assets:Bank 30.00 EUR
# prints out:
# 2023/07/20 Sushi Bar
#   Expenses:Restaurant                       30.00 EUR
#   Assets:Bank
```
### [on-modify-log](./on-modify-log.clj)
A [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) hook to log latest modified task. This script is _not_ installed in `~/.local/bin`. Instead, it requires you to copy it to your Taskwarrior's hooks folder. This is usually `~/.task/hooks`. Every time a task is modified, it writed the UUID of the task in a file called `last-modified.data` in your Taskwarrior's `data.location`. **This hook is required for the [resumetask](#resumetask) script to work.**

### [projectsof](./projectsof.clj)
Searches the current working directory for project directories of a certain type. For example, calling `projectsof java` will return all the directories which are java projects. **Requires [`rg`](https://github.com/BurntSushi/ripgrep) to run.**

#### Flags
- `-n` - displays numbered rows
- `-i NUMBER` - outputs directory with line number `-i`

#### Currently supported project types
1. Clojure/Clj
2. Java

### [resumetask](./resumetask.clj)
Ever wanted to just restart the [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task you stopped right before a break? With this script, you can just pick up where you left off by calling `resumetask`. No more trying to figure what ID your task has! **This script requires the [on-modify-log](#on-modify-log) hook to work and the [taskinfo](#taskinfo) script to work.**

#### Why use hooks?
Some people suggest having a shell alias that starts a task and exports it as an environment variable. But since I use [Syncthing](https://github.com/syncthing/syncthing) to sync my tasks across devices, this will not work if I stopped a task on one device and want to resume it on another. By saving the last modified task's UUID in Taskwarrior's `data.location`, I can have the UUID synced as well.

### [startnewtask](./startnewtask.clj)
Creates and immediately starts a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task. Use this as you would `task add`.
```sh
task add +admin +bookkeeping track finance # adds a task to Taskwarrior
startnewtask +admin +bookkeeping track finance # adds and starts task
```
### [stoptasks](./stoptasks.clj)
Stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks. Every tried `task stop` and gotten an error? Yeah, me too. Now you can stop all active tasks with a single `stoptasks`.

### [taskinfo](./taskinfo.clj)
Prints the attribute of a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task. Commands follow this format:
```sh
taskinfo TASKID TASKATTRIBUTE
# example: taskinfo 40 description
```
The [resumetask](#resumetask) script depends on this script.

## Change log
- [584d3d0](../../commit/584d3d04b3d9d2a9d1fdd79789e7c4908daa40be) - added [create-clj-minimal](../41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) shell script
- [4b8c492](../../commit/4b8c492ecd1725646dbff502a19a77cc73c52747) - added [stoptasks](../41de9d4fd0103c7b1cefa4b47439054353a59a91/stoptasks) shell script
- [41de9d4](../../commit/41de9d4fd0103c7b1cefa4b47439054353a59a91) - added [starttasks](../41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) shell script
- [8bd623e](../../commit/8bd623ef16068c4ed0ece1d1df32ed6bb0b210b8) - ported [create-clj-minimal](../41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) to Clojure. It is now called [cljminimal](./cljminimal.clj)
- [3a57c9a](../../commit/3a57c9abac263ceea7add9513b70868862b98d1d) - ported [starttasks](../41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) to Clojure. It is now called [startnewtask](./startnewtask.clj)
- [e610b0b](../../commit/e610b0b5c82580de74f6ccb644e9e092f9f7e130) - ported [stoptasks](./stoptasks.clj) to Clojure.
- [80e3f79](../../commit/80e3f792e56c5b620fa5ff1a6493c8b913188df7) - added [install](./install.clj) script
- [2ec63e7](../../commit/2ec63e7e77a2adb9f3b2e22090f85a911868f238) - added [uninstall](../2ec63e7e77a2adb9f3b2e22090f85a911868f238/uninstall-some-utils.clj) script
- [f1d42f7](../../commit/f1d42f7bc172d9ffdf51419d17b5d7792dabe70e) - removed the [uninstall](../2ec63e7e77a2adb9f3b2e22090f85a911868f238/uninstall-some-utils.clj) script in favor of a programmatically created uninstall script. Installing these scripts now automatically creates `uninstall-some-scripts`.
- [a020b2a](../../commit/a020b2aba3fdbcc132e53df2b4859d5aab88e9f1) - added [keepbooks](./keepbooks.clj)
- [2d46c23](../../commit/2d46c233a158950a3b2860f405a7dfb81484e06e) - fix [#1](../../issues/1)
- [a7c0817](../../commit/a7c081747dc0ec4404f6a17dc3f9141316cdc534) - added [on-modify-log](./on-modify-log.clj) Taskwarrior hook, [resumetask](./resumetask.clj) and updated [install](./install.clj) script
- [f2b0214](../../commit/f2b021434554a3491c5cf07aced3a33479e662d1) - added [taskinfo](./taskinfo.clj) 
- [e725018](../../commit/e7250185cc92cb0d2626b0048817ccd8a4e3cb5d) - added [jrun](./jrun.clj)
- [fd7b165](../../commit/fd7b165136f06fcd8c018401942c008ba0a261da) - added [projectsof](./projectsof.clj)
- [371c1ea](../../commit/371c1ea57bf5ebf3da98423552edba18d66f6957) - added clj to [projectsof](./projectsof.clj) and numbered output
