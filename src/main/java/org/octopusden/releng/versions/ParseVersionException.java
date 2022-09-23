package org.octopusden.releng.versions;

public class ParseVersionException extends RuntimeException {

    private static final long serialVersionUID = 8744279413057882216L;

    public ParseVersionException(String message) {
        super(message);
    }

    public ParseVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
