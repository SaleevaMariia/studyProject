package org.study.task.exception;

import org.study.task.api.TaskStatus;

public class InvalidTaskStatusTransitionException extends RuntimeException{
    public InvalidTaskStatusTransitionException( TaskStatus currentStatus,
                                                 TaskStatus newStatus){
        super("Invalid task status transition: " + currentStatus + " -> " + newStatus);
    }

}
