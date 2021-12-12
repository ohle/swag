package de.eudaemon.swag;

import java.awt.Dimension;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    StackTraceElement[] getStackTrace(int hashCode);

    Dimension getSize(int hashCode);
}
