package entu.timer.cli;

import static java.lang.Integer.parseInt;

import entu.timer.timers.History;
import entu.timer.timers.Record;
import entu.timer.timers.TimerService;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final Scanner inScanner;
    private final TimerService timerService;
    private final History history;

    public CommandLineInterface(Scanner inScanner, final TimerService timerService,
            final History history) {
        this.inScanner = inScanner;
        this.timerService = timerService;
        this.history = history;
    }

    public void start() {
        while (true) {
            prompt();
            final String command = inScanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(command)) {
                System.exit(0);
            }

            if ("history".equalsIgnoreCase(command)) {
                history.print();
                continue;
            }

            if ("next".equalsIgnoreCase(command)) {
                final List<Record> lastTimers = history.last(5);
                double averageMultiplier = 0.0;
                int actualAmount = lastTimers.size();
                for (int i = 0; i < actualAmount - 1; ++i) {
                    averageMultiplier +=
                            1.0 * lastTimers.get(i + 1).getDurationSeconds() / lastTimers.get(i)
                                    .getDurationSeconds();
                }
                averageMultiplier /= (actualAmount - 1);

                final int nextDuration;
                if (averageMultiplier < 0.1 || actualAmount < 1) {
                    nextDuration = 1;
                } else {
                    nextDuration = (int) (averageMultiplier * lastTimers.get(actualAmount - 1)
                            .getDurationSeconds());
                }

                timerService.addTimer(nextDuration);

                continue;
            }

            if (command.length() > 1 && command.charAt(command.length() - 1) == 's') {
                final String secondsDurationStr = command.substring(0, command.length() - 1).trim();
                try {
                    timerService.addTimer(parseInt(secondsDurationStr));
                    continue;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "thought to be timer command, but can't identify as a number of seconds: "
                                    + secondsDurationStr);
                }
            }

            System.out.println("Unable to identify command: " + command);
        }
    }

    private void prompt() {
        System.out.println("history, <N>s - n seconds, exit");
    }
}
