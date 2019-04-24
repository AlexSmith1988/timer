# timer
Simple Java Console Timer with history

Commands:
- history - print all timers 
- exit - quit program
- \<n\>s - add new timer for n seconds
- next - excel-like automatic guessing of next interval based on up to 5 previous ones.

Changes implemented:


Todo:
- Rename main class ConsoleTimer
- Extract individual classes
- shorthand commands
- Gradle task for running, adding it into timer.sh and timer.bat
- Automatic file persisting
- Change Output to "timer <id> for <n> seconds started at <date-time>"
- Output date once in history if finish and start share date 
    - Good place for unit tests
- Add timer number in history
- stop/stop -n stop timer earlier
- add label on timer
- add stopwatch 
- Shorthand command h\<N\> for last N timers history
- Command sum\<N\> for duration sum of previous N timers
    - Convert to hh::mm::ss format
    - Add outputting wall clock time span during which this timers where logged
    - sum n m - sum all timer durations between n and m inclusive
- Refactor 
    - file package-private classes -> per class files
    - use standard library for command line interface
- Add setting to stop previous timer when a new one starts
- Add tasks
- Add plans
- Add essays/contemplations
- Add code complete
- Add *.bat and *.sh for fast running
- Generally add packaging
- Hosting of builds
