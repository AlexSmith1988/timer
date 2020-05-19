package entu.timer.cli;

import static entu.timer.cli.DurationType.AS_INCREMENT;
import static entu.timer.cli.DurationType.AS_INCREMENT_OF_INCREMENT;
import static entu.timer.cli.DurationType.PLAIN;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

import entu.timer.application.CommandExecutor;
import entu.timer.output.Output;
import entu.timer.timers.Service;
import entu.timer.timers.StopWatch;
import entu.timer.timers.Timer;
import entu.timer.timers.Timetable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final Output output;
    private final Scanner inScanner;
    private final Service service;
    private final Timetable timetable;
    private final StopWatch stopWatch;

    private String previousCommand;

    private final List<CommandExecutor> commandExecutors = new ArrayList<>();

    public CommandLineInterface(
            final Output output,
            final Scanner inScanner,
            final Service service,
            final Timetable timetable,
            final StopWatch stopWatch) {
        this.output = output;
        this.inScanner = inScanner;
        this.service = service;
        this.timetable = timetable;
        this.stopWatch = stopWatch;

        registerCommand(
                        "exit",
                        () -> {
                            timetable.persist();
                            System.exit(0);
                        })
                .registerCommand("timetable", timetable::print)
                .registerCommand(
                        "next",
                        () -> {
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
                        })
                .registerCommand("sound", service::soundCheck)
                .registerCommand("rollrun", Timer::rollRun)
                // todo fix to be previous day, i.e. ending right now
                .registerCommand(
                        "daysum",
                        () ->
                                output.print(
                                        toSecondsAndMinutesMessage(
                                                timetable
                                                        .getToday()
                                                        .mapToInt(Timer::getDurationSeconds)
                                                        .sum())))
                .registerCommand(
                        "runsum",
                        () ->
                                output.print(
                                        toSecondsAndMinutesMessage(
                                                timetable.getRun().stream()
                                                        .mapToInt(Timer::getDurationSeconds)
                                                        .sum())))
                .registerCommand(
                        "dayruns",
                        () ->
                                output.print(
                                        toSecondsAndMinutesMessage(
                                                timetable.getRun().stream()
                                                        .mapToInt(Timer::getDurationSeconds)
                                                        .sum())))
                .registerCommand(
                        "sum",
                        () ->
                                output.print(
                                        toSecondsAndMinutesMessage(
                                                timetable
                                                        .get()
                                                        .mapToInt(Timer::getDurationSeconds)
                                                        .sum())))
                .registerCommand("start", stopWatch::start)
                // todo fix stop command, check start command
                .registerCommand("stop", stopWatch::stop)
                .registerCommand(
                        "help",
                        () -> commandExecutors.forEach(executor -> output.print(executor.help())));
        ;
    }

    private CommandLineInterface registerCommand(String command, Runnable executor) {
        commandExecutors.add(CommandExecutor.forCommand(command, executor));
        return this;
    }

    public void start() {
        while (true) {
            prompt();
            execute(readCommand());
        }
    }

    private void prompt() {
        output.print("timetable, <N>s - n seconds, exit");
    }

    private String readCommand() {
        return inScanner.nextLine().trim();
    }

    private void execute(String command) {
        if ("r".equalsIgnoreCase(command) || "repeat".equalsIgnoreCase(command)) {
            execute(previousCommand);
        } else {
            previousCommand = command;

            if (commandExecutors.stream().anyMatch(executor -> executor.executeOnMatch(command))) {
                return;
            }

            new CommandExecutor() {

                private final List<String> secondTokens = asList("s", "sec", "second", "seconds");
                private final List<String> minuteTokens = asList("m", "min", "minute", "minutes");

                private int seconds;
                private int pluses;

                @Override
                public boolean executeOnMatch(String command) {
                    if (command.isEmpty()) return false;

                    pluses = 0;
                    for (pluses = 0; command.charAt(pluses) == '+'; ++pluses) {}
                    command = command.substring(pluses);

                    final int commandLength = command.length();

                    endsWithOneOfTokens(command, commandLength, minuteTokens);
                    return false;
                }

                private boolean endsWithOneOfTokens(
                        String command, int commandLength, List<String> tokens) {
                    boolean endsWithOneOfTokens = false;
                    for (String minuteToken : minuteTokens) {
                        final int tokenLength = minuteToken.length();
                        if (commandLength < tokenLength) continue;

                        if (minuteToken.equalsIgnoreCase(
                                command.substring(commandLength - tokenLength))) {
                            endsWithOneOfTokens = true;
                            break;
                        }
                    }

                    return endsWithOneOfTokens;
                }
            };

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
                        duration =
                                timetable.lastDuration()
                                        + timetable.lastIncrement()
                                        + baseDurationCoefficient;
                    } else {
                        duration = baseDurationCoefficient;
                    }

                    service.addTimer(duration, timetable.lastDuration());
                    return;

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
}

enum DurationType {
    PLAIN,
    AS_INCREMENT,
    AS_INCREMENT_OF_INCREMENT,
    AS_MULTIPLIER,
    AS_INCREMENT_MULTIPLIER
}
