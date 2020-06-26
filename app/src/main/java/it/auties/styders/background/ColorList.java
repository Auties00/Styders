package it.auties.styders.background;

import androidx.annotation.NonNull;

public class ColorList implements Cloneable {
    private int[] colors;
    private boolean filled;
    private boolean update;

    public ColorList(int[] colors, boolean filled, boolean update) {
        this.colors = colors;
        this.filled = filled;
        this.update = update;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private ColorList() {
        this.colors = new int[]{};
        this.filled = false;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    static ColorList empty() {
        return new ColorList();
    }

    static ColorList of(int[] array) {
        return new ColorList(array, true, false);
    }


    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public int[] getColors() {
        return colors;
    }
}
