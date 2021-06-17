/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.ballroom;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import org.jboss.hal.resources.Constants;
import org.jboss.hal.resources.Names;

public final class Format {

    private static final DateTimeFormat DATE_TIME_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
    private static final DateTimeFormat DATE_TIME_MEDIUM = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
    private static final DateTimeFormat TIME_MEDIUM = DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM);
    private static final DateTimeFormat TIMESTAMP = DateTimeFormat.getFormat("HH:mm:ss.SSS");
    private static final NumberFormat SIZE_FORMAT = NumberFormat.getFormat("#,##0.#");
    private static final Constants CONSTANTS = GWT.create(Constants.class);
    private static final String SPACE = " ";
    private static final String COMMA = ", ";


    public static String timestamp(Date date) {
        return date != null ? TIMESTAMP.format(date) : Names.NOT_AVAILABLE;
    }

    public static String time(Date date) {
        return date != null ? TIME_MEDIUM.format(date) : Names.NOT_AVAILABLE;
    }

    public static String shortDateTime(Date date) {
        return date != null ? DATE_TIME_SHORT.format(date) : Names.NOT_AVAILABLE;
    }

    public static String mediumDateTime(Date date) {
        return date != null ? DATE_TIME_MEDIUM.format(date) : Names.NOT_AVAILABLE;
    }

    public static String humanReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"}; //NON-NLS
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return SIZE_FORMAT.format(size / Math.pow(1024, digitGroups)) + SPACE + units[digitGroups];
    }

    /**
     * Formats the elapsed time (in milliseconds) to a human readable format: example 1 minute, 16 seconds.
     *
     * @param duration in milliseconds
     *
     * @return The string representation of the human readable format.
     */
    public static String humanReadableDuration(long duration) {
        if (duration < 1000) {
            return duration + " ms"; //NON-NLS
        }

        duration /= 1000;

        int sec = (int) duration % 60;
        duration /= 60;

        int min = (int) duration % 60;
        duration /= 60;

        int hour = (int) duration % 24;
        duration /= 24;

        int day = (int) duration;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day)
              .append(SPACE)
              .append(day > 1 ? CONSTANTS.days() : CONSTANTS.day());
        }
        // prints 0 hour in case days exists. Otherwise prints 2 days, 34 min, sounds weird.
        if (hour > 0 || (day > 0)) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            sb.append(hour)
              .append(SPACE)
              .append(hour > 1 ? CONSTANTS.hours() : CONSTANTS.hour());
        }
        if (min > 0) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            sb.append(min)
              .append(SPACE)
              .append(min > 1 ? CONSTANTS.minutes() : CONSTANTS.minute());
        }
        if (sec > 0) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            sb.append(sec)
              .append(SPACE)
              .append(sec > 1 ? CONSTANTS.seconds() : CONSTANTS.second());
        }
        return sb.toString();
    }

    /**
     * Formats the elapsed time (in nanoseconds) to a human readable format: example 1 minute, 16 seconds.
     *
     * @param duration in nanoseconds
     *
     * @return The string representation of the human readable format.
     */
    public static String humanReadableDurationNanoseconds(long duration) {
        long l = TimeUnit.NANOSECONDS.toMillis(duration);
        if (l > 0) {
            return humanReadableDuration(l);
        } else {
            return duration + " ns ";
        }
    }


    private Format() {
    }
}
