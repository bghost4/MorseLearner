package com.derpderphurr.morse;

public class CodeElement {
    public enum Type { MARK,SPACE }
    public final int units;
    public final Type type;

    public CodeElement(Type type,int units) {
        this.units = units;
        this.type = type;
    }

    public int getUnits() {
        return units;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CodeElement{" +
                "units=" + units +
                ", type=" + type +
                '}';
    }
}
