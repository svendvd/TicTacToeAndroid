package gmp.thiago.apps.tictactoe.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import gmp.thiago.apps.ai.model.AreaState;
import gmp.thiago.apps.tictactoe.R;

public class AreaView extends View {

    private Drawable xSymbol = getResources().getDrawable(R.drawable.x_symbol);
    private Drawable oSymbol = getResources().getDrawable(R.drawable.o_symbol);

    public AreaView(Context context) {
        super(context);
    }

    public AreaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AreaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setState(AreaState state) {
        setBackground(getStateDrawable(state));
    }

    private Drawable getStateDrawable(AreaState state){
        switch (state) {
            case O: return oSymbol;
            case X: return xSymbol;
            case NONE: return null;
            default: throw new RuntimeException("AreaState "+state+" needs to be implemented");
        }
    }

    /**
     * Update the symbol and fade-in the whole view.
     *
     * @param state
     */
    public void animateState(AreaState state) {
        Drawable stateDrawable = getStateDrawable(state);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setBackground(stateDrawable);
            }
        });
        animator.setDuration(750);
        animator.start();
    }

}
