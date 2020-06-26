package it.auties.styders.background;

import it.auties.styders.R;

public enum StydersStyle {
    DARK(R.style.DarkAppTheme), WHITE(R.style.WhiteAppTheme), DARK_GRAY(R.style.GrayAppTheme);
    private final int theme;

    StydersStyle(int theme) {
        this.theme = theme;
    }

    public int getTheme() {
        return theme;
    }
}
