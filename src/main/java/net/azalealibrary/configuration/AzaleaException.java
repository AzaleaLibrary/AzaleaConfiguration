package net.azalealibrary.configuration;

public class AzaleaException extends RuntimeException {

    private final String[] messages;

    public AzaleaException() {
        messages = new String[0];
    }

    public AzaleaException(String message, String... messages) {
        super(message);
        this.messages = messages;
    }

    public String[] getMessages() {
        return messages;
    }
}
