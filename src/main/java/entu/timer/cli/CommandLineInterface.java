package entu.timer.cli;

import static entu.timer.cli.DurationType.AS_INCREMENT;
import static entu.timer.cli.DurationType.AS_INCREMENT_OF_INCREMENT;
import static entu.timer.cli.DurationType.PLAIN;
import static java.lang.Integer.parseInt;

import entu.timer.output.Output;
import entu.timer.timers.Service;
import entu.timer.timers.Timer;
import entu.timer.timers.Timetable;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final Output output;
    private final Scanner inScanner;
    private final Service service;
    private final Timetable timetable;

    private String previousCommand;

    public CommandLineInterface(
            final Output output,
            final Scanner inScanner,
            final Service service,
            final Timetable timetable) {
        this.output = output;
        this.inScanner = inScanner;
        this.service = service;
        this.timetable = timetable;
    }

    public void start() {
        while (true) {
            prompt();
            String command = inScanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(command)) {
                System.exit(0);
            }

            if ("r".equalsIgnoreCase(command) || "repeat".equalsIgnoreCase(command)) {
                command = previousCommand;
            } else {
                previousCommand = command;
            }

            if ("timetable".equalsIgnoreCase(command)) {
                timetable.print();
                continue;
            }

            if ("next".equalsIgnoreCase(command)) {
                final List<Timer> lastTimers = timetable.last(5);
                double averageMultiplier = 0.0;
                int actualAmount = lastTimers.size();
                for (int i = 0; i < actualAmount - 1; ++i) {
                    averageMultiplier +=
                            1.0
                                    * lastTimers.get(i + 1).getDurationSeconds()
                                    / lastTimers.get(i).getDurationSeconds();
                }
                averageMultiplier /= (actualAmount - 1);

                final int nextDuration;
                if (averageMultiplier < 0.1 || actualAmount < 1) {
                    nextDuration = 1;
                } else {
                    nextDuration =
                            (int)
                                    (averageMultiplier
                                            * lastTimers
                                            .get(actualAmount - 1)
                                            .getDurationSeconds());
                }

                service.addTimer(nextDuration, timetable.lastDuration());

                continue;
            }

            if ("sum".equalsIgnoreCase(command)) {
                output.print(
                        toSecondsAndMinutesMessage(
                                timetable.get().mapToInt(Timer::getDurationSeconds).sum()));
                continue;
            }

            if (command.startsWith("sum")) {
                final String[] args = command.replaceAll(" {2}", " ").split(" ");
                if (args.length != 3) {
                    output.print(
                            "Wrong sum command format. Expected \"sum <from> <till>\". Actual \""
                                    + command
                                    + "\".");
                } else {
                    try {
                        final int from = parseInt(args[1]);
                        final int to = parseInt(args[2]);

                        output.print(
                                toSecondsAndMinutesMessage(
                                        timetable.get(from, to).mapToInt(Timer::getDurationSeconds)
                                                .sum())
                                        + " - sum from "
                                        + from
                                        + " to "
                                        + to);
                    } catch (final NumberFormatException nfe) {
                        output.print(
                                "Wrong sum command format. Args are expected to be numbers. Actual \""
                                        + command
                                        + "\".");
                    }
                }
                continue;
            }

            if (command.length() > 1 && command.charAt(command.length() - 1) == 's') {
                String durationStr = command.substring(0, command.length() - 1).trim();

                DurationType durationType = PLAIN;
                if (durationStr.startsWith("++")) {
                    durationType = AS_INCREMENT_OF_INCREMENT;
                    durationStr = durationStr.substring(2);
                } else if (durationStr.startsWith("+")) {
                    durationType = AS_INCREMENT;
                    durationStr = durationStr.substring(1);
                }

                final int baseDurationCoefficient;
                try {
                    baseDurationCoefficient = parseInt(durationStr);

                    final int duration;
                    if (durationType == AS_INCREMENT) {
                        duration = timetable.lastDuration() + baseDurationCoefficient;
                    } else if (durationType == AS_INCREMENT_OF_INCREMENT) {
                        duration = timetable.lastDuration() + timetable.lastIncrement()
                                + baseDurationCoefficient;
                    } else {
                        duration = baseDurationCoefficient;
                    }

                    service.addTimer(duration, timetable.lastDuration());
                    continue;

                } catch (NumberFormatException e) {
                    output.print(
                            "thought to be timer command, but can't identify as a number of seconds: "
                                    + durationStr);
                }

            }

            output.print("Unable to identify command: " + command);
        }
    }

    String toSecondsAndMinutesMessage(final int seconds) {
        return seconds + " in seconds or " + seconds / 60 + ":" + seconds % 60 + " in min:sec";
    }

    private void prompt() {
        output.print("timetable, <N>s - n seconds, exit");
    }
}

enum DurationType {
    PLAIN,
    AS_INCREMENT,
    AS_INCREMENT_OF_INCREMENT,
    AS_MULTIPLIER,
    AS_INCREMENT_MULTIPLIER
}

