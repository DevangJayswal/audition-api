package com.audition.common.logging;

import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    public void debug(final Logger logger, final String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }

    public void warn(final Logger logger, final String message, final Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, args);
        }
    }

    public void error(final Logger logger, final String message, final Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(message, args);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    /**
     * Creates a formatted string representation of a ProblemDetail object.
     * <p>
     * This method generates a multi-line string containing key information from the provided ProblemDetail, including
     * its status, title, detail, type (if present), and instance (if present).
     *
     * @param standardProblemDetail The ProblemDetail object to format
     * @return A string representation of the ProblemDetail
     */
    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        // TODO Add implementation here.
        StringBuilder sb = new StringBuilder();
        sb.append("Problem Detail: \n");
        sb.append("Status: ").append(standardProblemDetail.getStatus()).append("\n");
        sb.append("Title: ").append(standardProblemDetail.getTitle()).append("\n");
        sb.append("Detail: ").append(standardProblemDetail.getDetail()).append("\n");
        URI type = standardProblemDetail.getType();
        sb.append("Type: ").append(type).append("\n");
        URI instance = standardProblemDetail.getInstance();
        if (instance != null) {
            sb.append("Instance: ").append(instance).append("\n");
        }
        return sb.toString();
    }

    /**
     * Creates a formatted error message string with an error code and message.
     * <p>
     * This method combines an integer error code and a descriptive message into a single, formatted string. This is
     * useful for creating consistent error messages across the application.
     *
     * @param errorCode An integer representing the error code
     * @param message   A string containing a descriptive error message
     * @return A formatted string containing both the error code and message
     */
    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        // TODO Add implementation here.
        return String.format("Error Code: %d, Message: %s", errorCode, message);
    }
}
