package it.auties.styders.customization;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import it.auties.styders.R;
import it.auties.styders.background.StydersStyle;

public class SelectThemeActivity extends AppCompatActivity {
    private StydersStyle stydersStyle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.style_message);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        this.stydersStyle = StydersStyle.DARK_GRAY;

        RadioGroup group = findViewById(R.id.startStyleOptions);

        View bg = findViewById(R.id.themeChooserRoot);
        AppCompatButton button = findViewById(R.id.themeBtn);
        RadioButton first = findViewById(R.id.startStyleOptionsOne);
        RadioButton second = findViewById(R.id.startStyleOptionsTwo);
        RadioButton third = findViewById(R.id.startStyleOptionsThree);

        group.setOnCheckedChangeListener((gr, checkedId) -> {
            if (checkedId == R.id.startStyleOptionsOne) {
                getWindow().setStatusBarColor(Color.WHITE);
                getWindow().setNavigationBarColor(Color.WHITE);
                bg.setBackgroundColor(getResources().getColor(R.color.white));
                button.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                button.setTextColor(Color.WHITE);
                first.setTextColor(Color.BLACK);
                second.setTextColor(Color.BLACK);
                third.setTextColor(Color.BLACK);
                first.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                second.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                third.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                this.stydersStyle = StydersStyle.WHITE;
            } else if (checkedId == R.id.startStyleOptionsTwo) {
                getWindow().setStatusBarColor(Color.BLACK);
                getWindow().setNavigationBarColor(Color.BLACK);
                bg.setBackgroundColor(getResources().getColor(R.color.black));
                button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                button.setTextColor(Color.BLACK);
                first.setTextColor(Color.WHITE);
                second.setTextColor(Color.WHITE);
                third.setTextColor(Color.WHITE);
                first.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                second.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                third.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                this.stydersStyle = StydersStyle.DARK;
            } else if (checkedId == R.id.startStyleOptionsThree) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.gray_background));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_background));
                bg.setBackgroundColor(getResources().getColor(R.color.gray_background));
                button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                button.setTextColor(Color.BLACK);
                first.setTextColor(Color.WHITE);
                second.setTextColor(Color.WHITE);
                third.setTextColor(Color.WHITE);
                first.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                second.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                third.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                this.stydersStyle = StydersStyle.DARK_GRAY;
            }
        });

        button.setOnClickListener(v -> {
            Intent intent = new Intent(SelectThemeActivity.this, LockscreenMessageActivity.class);
            intent.putExtra("stydersStyle", stydersStyle.name());
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {

    }
}
