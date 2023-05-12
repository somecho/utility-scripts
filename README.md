# Somē's utility scripts

Here are some utility scripts I wrote for myself. At first I wrote the scripts in a shell scripting language. But then I discovered [Babashka](https://github.com/babashka/babashka) and I love Clojure. I decided to port all the scripts to Clojure instead. You will need [Babashka](https://github.com/babashka/babashka) to run these scripts. These are helper tools for [Clj](https://clojure.org/guides/deps_and_cli), Java, [Ledger](https://github.com/ledger/ledger) and [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior).

### [Scripts](#scripts) included:
1. [accountsof](#accountsof) - outputs the name of all accounts used in a [Ledger](https://github.com/ledger/ledger) journal file
2. [cljminimal](#cljminimal) - creates an ultra barebones deps.edn [clj](https://clojure.org/guides/deps_and_cli) project for quick hacking
3. [jrun](#jrun) - single file Java runner 
4. [keepbooks](#keepbooks) - simple transaction entry helper for [Ledger](https://github.com/ledger/ledger) CLI accounting. Supports interactive entry.
5. [on-modify-log](#on-modify-log) - a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) hook to log the latest modified task
6. [projectsof](#projectsof) - finds directories of certain project types
7. [resumetask](#resumetask) - resumes latest modified [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
8. [startnewtask](#startnewtask) - creates and starts a new [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
9. [stoptasks](#stoptasks) - stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks
10. [taskinfo](#taskinfo) - prints the attribute of a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
 
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
 
### [accountsof](./accountsof.clj)
Outputs the names of all the accounts used in a [Ledger](https://github.com/ledger/ledger) journal file. Example: `accountsof LEDGERFILE`.

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
A helper script to enter a simple transaction into a [Ledger](https://github.com/ledger/ledger) file. 

#### Single entry mode
The script has the following format in single entry mode:
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

#### Interactive mode
`keepbooks` also supports interactive entry. Simply call `keepbooks -f LEDGERFILE` without any arguments and you will be prompted to enter your transaction.

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
