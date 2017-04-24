package com.icebreakers.nexxus.activities;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import com.icebreakers.nexxus.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OnboardingActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBackButtonTranslationWrapper()
            .setEnterTranslation(new IViewTranslation() {
                @Override
                public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                    view.setAlpha(percentage);
                }
            });

        addSlide(new SlideFragmentBuilder()
                     .backgroundColor(R.color.first_slide_background)
                     .buttonsColor(R.color.first_slide_buttons)
                     .image(R.drawable.attendees)
                     .title(getString(R.string.onboarding_find_attenedees))
                     .description(getString(R.string.onboarding_find_attenedees_subtitle))
                     .build());

        addSlide(new SlideFragmentBuilder()
                     .backgroundColor(R.color.second_slide_background)
                     .buttonsColor(R.color.second_slide_buttons)
                     .image(R.drawable.messaging)
                     .title(getString(R.string.onboarding_message))
                     .description(getString(R.string.onboarding_message_subtitle))
                     .build());

        addSlide(new SlideFragmentBuilder()
                     .backgroundColor(R.color.third_slide_background)
                     .buttonsColor(R.color.third_slide_buttons)
                     .image(R.drawable.search)
                     .title(getString(R.string.onboarding_search))
                     .description(getString(R.string.onboarding_search_subtitle))
                     .build());

        addSlide(new SlideFragmentBuilder()
                     .backgroundColor(R.color.fourth_slide_background)
                     .buttonsColor(R.color.fourth_slide_buttons)
                     .image(R.drawable.nearby)
                     .title(getString(R.string.onboarding_nearby))
                     .description(getString(R.string.onboarding_nearby_subtitle))
                     .build(),
                 new MessageButtonBehaviour(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
                     }
                 }, getString(R.string.start_networking)));

    }

    @Override
    public void onFinish() {
        super.onFinish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}