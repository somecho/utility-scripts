# Somē's utility scripts

Here are some utility scripts I wrote for myself. At first I wrote the scripts in a shell scripting language. But then I discovered [Babashka](https://github.com/babashka/babashka) and I love Clojure. I decided to port all the scripts to Clojure instead. You will need [Babashka](https://github.com/babashka/babashka) to run these scripts. These are helper tools for [Clj](https://clojure.org/guides/deps_and_cli), [Ledger](https://github.com/ledger/ledger) and [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior).

### [Scripts](#scripts) included:
1. [cljminimal](#cljminimal) - creates an ultra barebones deps.edn [clj](https://clojure.org/guides/deps_and_cli) project for quick hacking
2. [keepbooks](#keepbooks) - simple transaction entry helper for [Ledger](https://github.com/ledger/ledger) CLI accounting
3. [on-modify-log](#on-modify-log) - a [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) hook to log the latest modified task
4. [resumetask](#resumetask) - resumes latest modified [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
5. [startnewtask](#startnewtask) - creates and starts a new [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task
6. [stoptasks](#stoptasks) - stops all active [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) tasks
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

### [resumetask](./resumetask.clj)
Ever wanted to just restart the [Taskwarrior](https://github.com/GothenburgBitFactory/taskwarrior) task you stopped right before a break? With this script, you can just pick up where you left off by calling `resumetask`. No more trying to figure what ID your task has! **This script requires the [on-modify-log](#on-modify-log) hook to work. 

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

## Change log
- [584d3d0](https://github.com/somecho/utility-scripts/commit/584d3d04b3d9d2a9d1fdd79789e7c4908daa40be) - added [create-clj-minimal](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) shell script
- [4b8c492](https://github.com/somecho/utility-scripts/commit/4b8c492ecd1725646dbff502a19a77cc73c52747) - added [stoptasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/stoptasks) shell script
- [41de9d4](https://github.com/somecho/utility-scripts/commit/41de9d4fd0103c7b1cefa4b47439054353a59a91) - added [starttasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) shell script
- [8bd623e](https://github.com/somecho/utility-scripts/commit/8bd623ef16068c4ed0ece1d1df32ed6bb0b210b8) - ported [create-clj-minimal](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/create-clj-minimal) to Clojure. It is now called [cljminimal](./cljminimal.clj)
- [3a57c9a](https://github.com/somecho/utility-scripts/commit/3a57c9abac263ceea7add9513b70868862b98d1d) - ported [starttasks](https://github.com/somecho/utility-scripts/blob/41de9d4fd0103c7b1cefa4b47439054353a59a91/starttask) to Clojure. It is now called [startnewtask](./startnewtask.clj)
- [e610b0b](https://github.com/somecho/utility-scripts/commit/e610b0b5c82580de74f6ccb644e9e092f9f7e130) - ported [stoptasks](./stoptasks.clj) to Clojure.
- [80e3f79](https://github.com/somecho/utility-scripts/commit/80e3f792e56c5b620fa5ff1a6493c8b913188df7) - added [install](./install.clj) script
- [2ec63e7](https://github.com/somecho/utility-scripts/commit/2ec63e7e77a2adb9f3b2e22090f85a911868f238) - added [uninstall](https://github.com/somecho/utility-scripts/blob/2ec63e7e77a2adb9f3b2e22090f85a911868f238/uninstall-some-utils.clj) script
- [f1d42f7](https://github.com/somecho/utility-scripts/commit/f1d42f7bc172d9ffdf51419d17b5d7792dabe70e) - removed the [uninstall](https://github.com/somecho/utility-scripts/blob/2ec63e7e77a2adb9f3b2e22090f85a911868f238/uninstall-some-utils.clj) script in favor of a programmatically created uninstall script. Installing these scripts now automatically creates `uninstall-some-scripts`.
- [a020b2a](https://github.com/somecho/utility-scripts/commit/a020b2aba3fdbcc132e53df2b4859d5aab88e9f1) - added [keepbooks](./keepbooks.clj)
- [2d46c23](https://github.com/somecho/utility-scripts/commit/2d46c233a158950a3b2860f405a7dfb81484e06e) - fix [#1](https://github.com/somecho/utility-scripts/issues/1)
- [a7c0817](https://github.com/somecho/utility-scripts/commit/a7c081747dc0ec4404f6a17dc3f9141316cdc534) - added [on-modify-log](./on-modify-log.clj) Taskwarrior hook, [resumetask](./resumetask.clj) and updated [install](./install.clj) script
