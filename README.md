# timer
Simple Java Console Timer with history

Commands:
- history - print all timers 
- exit - quit program
- \<n\>s - add new timer for n seconds
- next - excel-like automatic guessing of next interval based on up to 5 previous ones
- sum n m - sum of durations of timers with ids between n and m


Todo:
- Command sum\<N\> for duration sum of previous N timers
    - Convert to hh::mm::ss format
    - Add outputting wall clock time span during which this timers where logged
    - sum n m - sum all timer durations between n and m inclusive
- Unit tests!
- Extract commands
- use standard library for command line interface
- shorthand commands
- Gradle task for running, adding it into timer.sh and timer.bat
- Automatic file persisting
- stop/stop -n stop timer earlier
- add label on timer
- add stopwatch 
- Shorthand command h\<N\> for last N timers history
- Add setting to stop previous timer when a new one starts
- Add tasks
- Add plans
- Add essays/contemplations
- Add code complete
- Add *.bat and *.sh for fast running
- Generally add packaging
- Hosting of builds
