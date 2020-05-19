package entu.timer.application;

public class CommandExecutor {

    public static CommandExecutor forCommand(String command, Runnable execute) {
        return new CommandExecutor() {
            @Override
            public String label() {
                return command;
            }

            @Override
            public void execute(String command) {
                execute.run();
            }
        };
    }

    public String help() {
        final String comment = comment();
        if (null == comment || comment.isEmpty()) return label();

        return label() + " - " + comment;
    }

    public String label() {
        return "";
    }

    public String comment() {
        return "";
    }

    public boolean executeOnMatch(String command) {
        if (!matches(command)) return false;

        execute(command);
        return true;
    }

    public boolean matches(String command) {
        return null != command && command.equalsIgnoreCase(label());
    }

    public void execute(String command) {}
}
