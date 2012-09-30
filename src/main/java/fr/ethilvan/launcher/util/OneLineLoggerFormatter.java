package fr.ethilvan.launcher.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class OneLineLoggerFormatter extends Formatter {

    private static final DateFormat dateFormat =
            new SimpleDateFormat("[dd/MM-hh:mm:ss]");

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date(record.getMillis())));
        builder.append(" ");
        builder.append(record.getLevel().getName().toUpperCase());
        builder.append(": ");
        builder.append(formatMessage(record));
        builder.append(" (@");
        builder.append(record.getSourceClassName());
        builder.append("#");
        builder.append(record.getSourceMethodName());
        builder.append(")\n");

        return builder.toString();
    }
}
