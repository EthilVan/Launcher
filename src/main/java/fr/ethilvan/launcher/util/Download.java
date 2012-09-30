package fr.ethilvan.launcher.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;

public abstract class Download<T extends OutputStream>
        extends HttpExchange {

    public static class Error {

        private String message;

        public Error(String message) {
            this.message = message;
        }

        public Error(int code, String reason) {
            this("HTTP Error : " + code + " => " + reason);
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ExceptionError extends Error {

        private final Throwable cause;

        public ExceptionError(String message, Throwable cause) {
            super(message);
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }
    }

    private final T output;

    public Download(String url, T output) {
        super();
        setURL(url);
        this.output = output;
    }

    protected abstract void onError(Error error);

    protected void onLengthKnown(int length) {
    }

    protected void onProgress(int progress) {
    }

    protected void onComplete(T output) {
    }

    @Override
    protected void onConnectionFailed(Throwable throwable) {
        onError(new ExceptionError("Connection failed", throwable));
    }

    @Override
    protected void onExpire() {
        onError(new Error("Expiration"));
    }

    @Override
    protected void onException(Throwable throwable) {
        onError(new ExceptionError("Exception", throwable));
    }

    @Override
    protected void onResponseStatus(Buffer http, int code, Buffer reasonBuf) {
        if (code != 200) {
            onError(new Error(code, reasonBuf.toString(Util.UTF8)));
            cancel();
        }
    }

    @Override
    protected void onResponseHeader(Buffer name, Buffer value) {
        if (getStatus() != HttpExchange.STATUS_CANCELLED
                && name.toString().equals("Content-Length")) {
            int length = Integer.parseInt(value.toString());
            onLengthKnown(length);
        }
    }

    @Override
    protected void onResponseContent(Buffer content) {
        int progress = content.length();
        try {
            content.writeTo(output);
        } catch (IOException exc) {
            Logger.getLogger(Download.class.getName())
                    .log(Level.WARNING,
                            "Cannot convert downloaded text to UTF-8", exc);
        }
        onProgress(progress);
    }

    @Override
    protected void onResponseComplete() {
        IOUtils.closeQuietly(output);
        onComplete(output);
    }
}
