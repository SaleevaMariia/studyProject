package org.study.task.domain;

public class Priority implements Comparable<Priority>{

    private final int value;

    public Priority(int value) {
        if(value < 1 || value > 5){
            throw new IllegalArgumentException("Value should be between 1 and 5");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Priority o) {
        return Integer.compare(value, o.value);
    }
}
