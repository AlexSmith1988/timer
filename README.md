# timer
Simple Java Console Timer with history

Commands:
    history - list all set timers 
    exit
    <N>s - new timer for n seconds

Todo:
- Automatic file persisting
- Timer number in history
- Shorthand command h<N> for last N timers history
- Command sum<N> for duration sum of previous N timers
    - Convert to hh::mm::ss format
    - Add outputting wallclock tim span during which this timers where logged
- Output date once in history if finish and start share date 
    - Good place for unit tests
- Refactor 
    - main class name -> ConsoleTimer
    - file package-private classes -> per class files
- Add *.bat and *.sh for fast running
- Generally add packaging
- Hosting of builds
