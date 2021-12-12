package de.eudaemon.swag;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    StackTraceElement[] getStackTrace(int hashCode);
}
