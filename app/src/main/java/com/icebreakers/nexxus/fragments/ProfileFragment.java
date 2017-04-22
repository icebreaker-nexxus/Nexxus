package com.icebreakers.nexxus.fragments;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.helpers.NestedScrollViewBehavior;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.helpers.SimilaritiesFinder;
import com.icebreakers.nexxus.listeners.MessageClickEvent;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;
import org.parceler.Parcels;

import java.util.List;

import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/5/17.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = NexxusApplication.BASE_TAG + ProfileFragment.class.getName();

    @BindView(R.id.tvName) TextView tvProfileName;
    @BindView(R.id.tvHeadline) TextView tvHeadline;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.commonSection) LinearLayout commonSection;
    @BindView(R.id.icebreakerSection) LinearLayout icebreakerSection;
    @BindView(R.id.cardViewExperience) CardView experienceCardView;
    @BindView(R.id.linearLayoutExperienceSection) LinearLayout linearLayoutExperienceSection;
    @BindView(R.id.cardViewEducation) CardView educationCardView;
    @BindView(R.id.linearLayoutEducationSection) LinearLayout linearLayoutEducationSection;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_background) View toolbarBackground;

    private Profile profile;
    private Similarities similaritiesWithLoggedInMember;
    private boolean isSelfView;
    private MessageClickEvent messageClickEvent;

    public static ProfileFragment newInstance(Profile profile) {

        Bundle args = new Bundle();
        args.putParcelable(PROFILE_EXTRA, Parcels.wrap(profile));
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        profile = Parcels.unwrap(getArguments().getParcelable(PROFILE_EXTRA));
        Profile cuurentProfile = ProfileHolder.getInstance(getContext()).getProfile();
        similaritiesWithLoggedInMember = SimilaritiesFinder.findSimilarities(cuurentProfile, profile);
        isSelfView = profile.id.equals(cuurentProfile.id);
        messageClickEvent = (MessageClickEvent) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        layoutParams.setBehavior(new NestedScrollViewBehavior());

        tvProfileName.setText(String.format(getString(R.string.full_name, profile.firstName, profile.lastName)));
        tvHeadline.setText(profile.headline);
        Glide.with(getActivity()).load(profile.pictureUrl).into(ivProfileImage);
        insertCommonSection();
        insertExperienceSection();
        insertEducationSection();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setupAnimationOnEnter(view);
        } else {
            toolbarBackground.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_action, menu);
        if (isSelfView) {
            menu.findItem(R.id.miMessage).setVisible(false);
        }
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.miMessage:
                this.messageClickEvent.onMessageClickEvent(profile);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupAnimationOnEnter(View rootView) {
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.v(TAG, "Starting animation");
                view.removeOnLayoutChangeListener(this);
                int x = toolbarBackground.getRight();
                int y = toolbarBackground.getBottom();

                int endRadius = (int) Math.hypot(rootView.getWidth(), rootView.getHeight());

                Animator reveal = ViewAnimationUtils.createCircularReveal(toolbarBackground, x, y, 0, endRadius);
                toolbarBackground.setVisibility(View.VISIBLE);
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(3000);
                reveal.start();
            }
        });

    }

    private void insertCommonSection() {

        if (isSelfView) {
            commonSection.setVisibility(View.GONE);
            icebreakerSection.setVisibility(View.GONE);
            return;
        }

        if (similaritiesWithLoggedInMember == null || similaritiesWithLoggedInMember.numOfSimilarities == 0) {
            commonSection.setVisibility(View.GONE);
            icebreakerSection.setVisibility(View.VISIBLE);
            return;
        }

        icebreakerSection.setVisibility(View.GONE);
        if (similaritiesWithLoggedInMember.similarPositions != null) {
            for (Profile.Position position : similaritiesWithLoggedInMember.similarPositions) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.common_item, commonSection, false);
                CommonItemViewHolder commonItemViewHolder = new CommonItemViewHolder(v);
                commonItemViewHolder.commonImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_work));
                commonItemViewHolder.commonAttributeText.setText(String.format(getString(R.string.worked_at), position.companyName));
                commonSection.addView(v);
            }
        }

        if (similaritiesWithLoggedInMember.similarEducations != null) {
            for (Profile.Education education : similaritiesWithLoggedInMember.similarEducations) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.common_item, commonSection, false);
                CommonItemViewHolder commonItemViewHolder = new CommonItemViewHolder(v);
                commonItemViewHolder.commonImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_education));
                commonItemViewHolder.commonAttributeText.setText(String.format(getString(R.string.studied_at), education.schoolName));
                commonSection.addView(v);
            }
        }
    }

    private void insertExperienceSection() {
        List<Profile.Position> positionList = profile.positionList;
        if (positionList == null || positionList.size() == 0) {
            experienceCardView.setVisibility(View.GONE);
            return;
        }

        for (Profile.Position position : positionList) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.profile_section_item, linearLayoutExperienceSection, false);
            SectionItemViewHolder sectionItemViewHolder = new SectionItemViewHolder(v);

            if (position.title != null) {
                sectionItemViewHolder.sectionHeader.setText(position.title);
            } else {
                sectionItemViewHolder.sectionHeader.setVisibility(View.GONE);
            }

            if (position.companyName != null) {
                sectionItemViewHolder.sectionHeadline.setText(position.companyName);
            } else {
                sectionItemViewHolder.sectionHeadline.setVisibility(View.GONE);
            }

            if (position.startDate != null) {
                String text = position.endDate != null ? String.format(getString(R.string.from_to), position.startDate, position.endDate) :
                              String.format(getString(R.string.from_to_present), position.startDate);
                sectionItemViewHolder.sectionSubheadline.setText(text);
            } else {
                sectionItemViewHolder.sectionSubheadline.setVisibility(View.GONE);
            }
            linearLayoutExperienceSection.addView(v);
        }
    }

    private void insertEducationSection() {
        List<Profile.Education> educationList = profile.educationList;
        if (educationList == null || educationList.size() == 0) {
            educationCardView.setVisibility(View.GONE);
            return;
        }

        for (Profile.Education edu : educationList) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.profile_section_item, linearLayoutEducationSection, false);
            SectionItemViewHolder sectionItemViewHolder = new SectionItemViewHolder(v);

            if (edu.schoolName != null) {
                sectionItemViewHolder.sectionHeader.setText(edu.schoolName);
            } else {
                sectionItemViewHolder.sectionHeader.setVisibility(View.GONE);
            }

            sectionItemViewHolder.sectionHeadline.setVisibility(View.GONE);

            if (edu.startDate != null) {
                String text = edu.endDate != null ? String.format(getString(R.string.from_to), edu.startDate, edu.endDate) :
                              String.format(getString(R.string.from_to_present), edu.startDate);
                sectionItemViewHolder.sectionSubheadline.setText(text);
            } else {
                sectionItemViewHolder.sectionSubheadline.setVisibility(View.GONE);
            }
            linearLayoutEducationSection.addView(v);
        }
    }

    class CommonItemViewHolder {
        @BindView(R.id.ivCommonImage) ImageView commonImage;
        @BindView(R.id.tvCommonAttributeText) TextView commonAttributeText;

        public CommonItemViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }


    class SectionItemViewHolder {
        @BindView(R.id.section_header_text) TextView sectionHeader;
        @BindView(R.id.section_headline_text) TextView sectionHeadline;
        @BindView(R.id.section_subheadline_text) TextView sectionSubheadline;

        public SectionItemViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
