package nukem.chatroom.utils;

import static nukem.chatroom.constants.Constants.*;

public final class ErrorMessageUtils {

    private ErrorMessageUtils() {
    }

    public static String entityNotFound(final String entityType, final Long id) {
        return entityType + WITH + ID + SPACE + id + NOT_FOUND;
    }

}