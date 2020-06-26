package it.auties.styders.background;


import android.graphics.Color;

import androidx.annotation.NonNull;

public class ColorSequence implements Cloneable {
    private final ColorList sequenceInUse;
    private final ColorList firstSequence;
    private final ColorList secondSequence;
    private final ColorList thirdSequence;
    private final ColorList fourthSequence;
    private final ColorList fifthSequence;

    public ColorSequence(ColorList sequenceInUse, ColorList firstSequence, ColorList secondSequence, ColorList thirdSequence, ColorList fourthSequence, ColorList fifthSequence) {
        this.sequenceInUse = sequenceInUse;
        this.firstSequence = firstSequence;
        this.secondSequence = secondSequence;
        this.thirdSequence = thirdSequence;
        this.fourthSequence = fourthSequence;
        this.fifthSequence = fifthSequence;
    }

    public int getFilled() {
        int filled = 0;
        for (int x = 0; x < 5; x++) {
            if (fromInt(x).isFilled()) {
                filled++;
            }
        }

        return filled;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ColorList fromInt(int x) {
        switch (x) {
            case 0:
                return firstSequence;
            case 1:
                return secondSequence;
            case 2:
                return thirdSequence;
            case 3:
                return fourthSequence;
            case 4:
                return fifthSequence;
            default:
                throw new RuntimeException("Input must be between 0 and 4, got: " + x);
        }
    }

    public ColorList cardinal(int x) {
        switch (x) {
            case 0:
                return sequenceInUse;
            case 1:
                return firstSequence;
            case 2:
                return secondSequence;
            case 3:
                return thirdSequence;
            case 4:
                return fourthSequence;
            case 5:
                return fifthSequence;
            default:
                throw new RuntimeException("Input must be between 0 and 5, got: " + x);
        }
    }


    public ColorSequence asCopy() {
        try {
            return (ColorSequence) clone();
        } catch (CloneNotSupportedException e) {
            throw new ClassCastException("Cannot cast class to object!");
        }
    }

    private ColorSequence() {
        this.sequenceInUse = ColorList.of(new int[]{
                Color.RED,
                Color.parseColor("#FFFF00FF"),
                Color.BLUE,
                Color.parseColor("#ff00ffff"),
                Color.GREEN,
                Color.YELLOW});
        this.firstSequence = ColorList.of(new int[]{
                Color.RED,
                Color.parseColor("#FFFF00FF"),
                Color.BLUE,
                Color.parseColor("#ff00ffff"),
                Color.GREEN,
                Color.YELLOW});
        this.secondSequence = ColorList.empty();
        this.thirdSequence = ColorList.empty();
        this.fourthSequence = ColorList.empty();
        this.fifthSequence = ColorList.empty();
    }

    static ColorSequence empty() {
        return new ColorSequence();
    }

    public ColorList getSequenceInUse() {
        return sequenceInUse;
    }
}
