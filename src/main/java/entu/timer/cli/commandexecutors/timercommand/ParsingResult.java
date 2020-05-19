package entu.timer.cli.commandexecutors.timercommand;

public class ParsingResult {

    private static final int invalidData = -1;
    static final ParsingResult NOT_A_TIMER_COMMAND = new ParsingResult(invalidData, invalidData);

    private final int amountOfPlusSigns;
    private final int secondsValue;

    public ParsingResult(int amountOfPlusSigns, int secondsValue) {
        this.amountOfPlusSigns = amountOfPlusSigns;
        this.secondsValue = secondsValue;
    }

    public int getAmountOfPlusSigns() {
        return amountOfPlusSigns;
    }

    public int getSecondsValue() {
        return secondsValue;
    }

    public boolean isProperTimerCommand() {
        return NOT_A_TIMER_COMMAND != this;
    }
}
