package it.auties.styders.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import it.auties.styders.R;
import it.auties.styders.background.ColorList;
import it.auties.styders.background.ColorSequence;
import it.auties.styders.background.StydersStyle;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.main.MainActivity;

public class ColorSequencesView extends BottomSheetDialogFragment {
    private final MainActivity mainActivity = MainActivity.getMainActivity();
    private final  WallpaperSettings settings = mainActivity.getSettings();
    private final ColorSequence colorSequence = settings.getColorSequences().asCopy();
    private AppCompatButton button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_color_sequences, container, false);
        this.button = view.findViewById(R.id.addSequence);

        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;

            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);

            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        update(view);

        LinearLayout selectedColor = view.findViewById(R.id.currentColorLayout);
        selectedColor.setBackgroundColor(settings.getStydersStyle() != StydersStyle.WHITE ? getResources().getColor(R.color.strong_gray) : Color.parseColor("#f0f0e6"));

        LinearLayout layout1 = view.findViewById(R.id.colorOptionTwo);
        layout1.setOnClickListener(view1 -> {
            if (scrollUp(1)) {
                hideIf(button);
            }
        });

        layout1.setOnLongClickListener(v -> {
            openMenu(view, layout1, 0);
            return true;
        });

        LinearLayout layout2 = view.findViewById(R.id.colorOptionThree);
        layout2.setOnClickListener(view1 -> {
            if (scrollUp(2)) {
                hideIf(button);
            }
        });

        layout2.setOnLongClickListener(v -> {
            openMenu(view, layout2, 1);
            return true;
        });


        LinearLayout layout3 = view.findViewById(R.id.colorOptionFour);
        layout3.setOnClickListener(view1 -> {
            if (scrollUp(3)) {
                hideIf(button);
            }
        });

        layout3.setOnLongClickListener(v -> {
            openMenu(view, layout3, 2);
            return true;
        });


        LinearLayout layout4 = view.findViewById(R.id.colorOptionFive);
        layout4.setOnClickListener(view1 -> {
            if (scrollUp(4)) {
                hideIf(button);
            }
        });

        layout4.setOnLongClickListener(v -> {
            openMenu(view, layout1, 3);
            return true;
        });


        LinearLayout layout5 = view.findViewById(R.id.colorOptionSix);
        layout5.setOnClickListener(view1 -> {
            if (scrollUp(5)) {
                hideIf(button);
            }
        });

        layout5.setOnLongClickListener(v -> {
            openMenu(view, layout1, 4);
            return true;
        });

        AppCompatButton cancelButton = view.findViewById(R.id.cancelButtonOne);
        if (cancelButton == null) {
            throw new NullPointerException("Missing cancel button!");
        }

        cancelButton.setOnClickListener(view1 -> dismiss());

        AppCompatButton continueButton = view.findViewById(R.id.confirmButtonOne);
        if (continueButton == null) {
            throw new NullPointerException("Missing confirm button!");
        }

        continueButton.setOnClickListener(view1 -> {
            settings.setColorSequences(colorSequence);

            int[] colors = colorSequence.getSequenceInUse().getColors();
            SquareView cardView = mainActivity.findViewById(R.id.colorOne);
            cardView.setCardBackgroundColor(colors[0]);

            SquareView cardView1 = mainActivity.findViewById(R.id.colorTwo);
            cardView1.setCardBackgroundColor(colors[1]);

            SquareView cardView2 = mainActivity.findViewById(R.id.colorThree);
            cardView2.setCardBackgroundColor(colors[2]);

            SquareView cardView3 = mainActivity.findViewById(R.id.colorFour);
            cardView3.setCardBackgroundColor(colors[3]);

            SquareView cardView4 = mainActivity.findViewById(R.id.colorFive);
            cardView4.setCardBackgroundColor(colors[4]);

            SquareView cardView5 = mainActivity.findViewById(R.id.colorSix);
            cardView5.setCardBackgroundColor(colors[5]);

            settings.setNewColor(true);

            dismiss();
        });

        hideIf(button);
        button.setOnClickListener(v -> {
            int index = 0;
            ColorList selected = null;
            while (index < 5) {
                ColorList current = colorSequence.fromInt(index);
                if (!current.isFilled()) {
                    selected = current;
                    break;
                }

                index++;
            }

            if (selected == null) {
                Toast.makeText(mainActivity, getResources().getString(R.string.maximum_num_of_sequences), Toast.LENGTH_LONG).show();
                return;
            }

            selected.setColors(colorSequence.getSequenceInUse().getColors().clone());
            selected.setFilled(true);
            selected.setUpdate(false);
            colorSequence.getSequenceInUse().setUpdate(false);
            button.setVisibility(View.GONE);
            dismiss();
        });

        return view;
    }

    private void update(View view) {
        for (int x = 0; x < 6; x++) {
            ColorList list = colorSequence.cardinal(x);
            if (x > 1 && list.isFilled()) {
                ColorList previous = colorSequence.cardinal(x - 1);
                if (!previous.isFilled()) {
                    previous.setFilled(true);
                    previous.setColors(list.getColors());
                    previous.setUpdate(false);
                    list.setFilled(false);
                    list.setColors(new int[]{});
                    list.setUpdate(false);
                }
            }
        }

        for (int x = 0; x < 6; x++) {
            ColorList list = colorSequence.cardinal(x);
            int[] toLoop = list.getColors();
            for (int y = 0; y < 6; y++) {
                SquareView cardView = view.findViewWithTag(String.valueOf(((x) * 6) + (y + 1)));
                if (!list.isFilled()) {
                    cardView.setVisibility(View.INVISIBLE);
                } else {
                    cardView.setCardBackgroundColor(toLoop[y]);
                }

                cardView.setFocusable(false);
            }
        }
    }


    private boolean scrollUp(int element) {
        if (getView() == null) {
            throw new RuntimeException("No root view found!");
        }

        if (element == 0) {
            return false;
        }


        ColorList list = colorSequence.fromInt(element - 1);
        if (!list.isFilled()) {
            return false;
        }

        if (list.isUpdate()) {
            list.setFilled(false);
            list.setUpdate(false);
            list.setColors(new int[]{});
        }

        int[] forStart = list.getColors().clone();

        colorSequence.getSequenceInUse().setColors(forStart);
        colorSequence.getSequenceInUse().setUpdate(false);

        update(getView());
        return true;
    }

    private void hideIf(AppCompatButton button) {
        if (!colorSequence.getSequenceInUse().isUpdate()) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    private void openMenu(View root, View view, int element) {
        PopupMenu popup = new PopupMenu(mainActivity, view);
        popup.getMenuInflater().inflate(R.menu.sequence_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (colorSequence.getFilled() <= 1) {
                Toast.makeText(mainActivity, getResources().getString(R.string.minimum_num_of_sequences), Toast.LENGTH_LONG).show();
                return true;
            }

            ColorList list = colorSequence.fromInt(element);
            list.setFilled(false);
            list.setUpdate(false);
            list.setColors(new int[]{});
            update(root);
            return true;
        });

        popup.show();
    }
}
