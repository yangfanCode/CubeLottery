package com.cp2y.cube.dialog;

/**
 * Created by js on 2017/1/18.
 */
public abstract class ConditionDialog {

    protected boolean isChecked = true;

    public boolean isChecked() {
        return isChecked;
    }

    public abstract void show();
    public abstract void reset();
}
