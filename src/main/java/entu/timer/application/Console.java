package entu.timer.application;

import entu.timer.cli.CommandLineInterface;
import entu.timer.output.Output;
import entu.timer.sound.Playback;
import entu.timer.timers.History;
import entu.timer.timers.TimerService;
import java.util.Scanner;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class Console {

    public static void main(String[] args)
            throws InvalidMidiDataException, MidiUnavailableException {

        final Output stdout = new Output(){};
        final Playback playback = new Playback(stdout);
        final History history = new History();
        final TimerService timerService = new TimerService(stdout, history, playback);
        new CommandLineInterface(stdout, new Scanner(System.in), timerService, history).start();
    }
}
