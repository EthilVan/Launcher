package fr.ethilvan.launcher.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;

public class BasicDownloader<T extends OutputStream>
        extends HttpExchange {

    private final T output;

    public BasicDownloader(String url, T output) {
        super();
        setURL(url);
        this.output = output;
    }

    protected void onError(int code, String reason) {
    }

    protected void onLengthKnown(int length) {
    }

    protected void onProgress(int progress) {
    }

    protected void onComplete(T output) {
    }

    @Override
    protected void onResponseStatus(Buffer http, int code, Buffer reasonBuf) {
        if (code != 200) {
            onError(code, reasonBuf.toString(Util.UTF8));
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
            Logger.getLogger(BasicDownloader.class.getName())
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
