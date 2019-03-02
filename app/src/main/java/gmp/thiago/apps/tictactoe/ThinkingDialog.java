package gmp.thiago.apps.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ThinkingDialog extends Dialog {
    private SharedPreferences prefs;
    private boolean darkTheme;

    @BindView(R.id.thinkinTv)
    TextView thinkingTv;

    public ThinkingDialog(@NonNull Context context, int style) {
        super(context, style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_thinking);

        ButterKnife.bind(this);
        thinkingTv.setText(getContext().getString(R.string.computer_thinking));
    }
}
