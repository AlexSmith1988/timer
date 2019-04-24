package entu.timer.application;

import entu.timer.cli.CommandLineInterface;
import entu.timer.output.Output;
import entu.timer.sound.Playback;
import entu.timer.timers.History;
import entu.timer.timers.Ids;
import entu.timer.timers.Service;
import java.util.Scanner;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class Console {

    public static void main(String[] args)
            throws InvalidMidiDataException, MidiUnavailableException {

        final Output stdout = new Output() {};
        final Playback playback = new Playback(stdout);
        final History history = new History();
        final Service service =
                new Service(stdout, history, playback, new Ids());
        new CommandLineInterface(stdout, new Scanner(System.in), service, history).start();
    }
}
