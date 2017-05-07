package com.icebreakers.nexxus.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.clients.GoogleCloudFunctionClient;
import com.icebreakers.nexxus.helpers.MessagesHelper;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.helpers.SimilaritiesFinder;
import com.icebreakers.nexxus.models.Message;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;
import com.icebreakers.nexxus.models.messaging.UIMessage;
import com.icebreakers.nexxus.persistence.Database;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import org.parceler.Parcels;

/**
 * Created by amodi on 4/12/17.
 */

public class MessagingFragment extends Fragment {

    private static final String PROFILE_EXTRA = "profile_extra";

    @BindView(R.id.messagesList) MessagesList messagesList;
    @BindView(R.id.input) MessageInput messageInput;
    @BindView(R.id.empty_state) RelativeLayout emptyStateRelativeLayout;
    @BindView(R.id.tvWhySerious) TextView whySerious;

    MessagesListAdapter<UIMessage> messagesListAdapter;
    Profile loggedInProfile;
    Profile messageToProfile;
    String messagesRowId;
    boolean resetMessage = false;
    boolean isEmptyStateVisible = true;

    ProfileHolder profileHolder;

    public static MessagingFragment newInstance(Profile messageToProfile) {

        Bundle args = new Bundle();
        args.putParcelable(PROFILE_EXTRA, Parcels.wrap(messageToProfile));
        MessagingFragment fragment = new MessagingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileHolder = ProfileHolder.getInstance(getContext());

        messageToProfile = Parcels.unwrap(getArguments().getParcelable(PROFILE_EXTRA));
        loggedInProfile = profileHolder.getProfile();
        messagesRowId = MessagesHelper.getMessageRowId(loggedInProfile, messageToProfile);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        ButterKnife.bind(this, view);

        messagesListAdapter = new MessagesListAdapter<>(loggedInProfile.id, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String s) {
                Glide.with(getContext()).load(s).into(imageView);
            }
        });
        setupInputMessageListener();
        setupNewIncomingMessageListener();
        messagesList.setAdapter(messagesListAdapter);
        setupPrecannedMessage();
        whySerious.setText(String.format(getString(R.string.why_so_serious), loggedInProfile.firstName));
        //bootstrapMessagesFromDatabase();
        return view;
    }

    private void setupInputMessageListener() {
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence charSequence) {
//                Message message = new Message();
//                message.timestamp = Calendar.getInstance().getTimeInMillis();
//                message.text = charSequence.toString();
//                message.id = UUID.randomUUID().toString();
//                message.senderId = loggedInProfile.id;
//                message.receiverId = messageToProfile.id;
//
//                // save message and messageRef to profile
//                profileHolder.saveMessage(messagesRowId, message);
                MessagesHelper.sendMessage(getContext(), loggedInProfile.id, messageToProfile.id, charSequence.toString());

                if (isEmptyStateVisible) {
                    emptyStateRelativeLayout.setVisibility(View.GONE);
                    isEmptyStateVisible = false;
                }
                emptyStateRelativeLayout.setVisibility(View.GONE);

//                // send push notification
                GoogleCloudFunctionClient.sendPushNotification(loggedInProfile.firstName, messageToProfile.id, loggedInProfile.id, charSequence.toString());

                return true;
            }
        });
    }


    private void setupNewIncomingMessageListener() {
        Database.instance().messagesTableReference().child(messagesRowId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (isEmptyStateVisible) {
                    emptyStateRelativeLayout.setVisibility(View.GONE);
                    isEmptyStateVisible = false;
                }
                if (!resetMessage) {
                    resetPrecannedMessage();
                }
                Message message = dataSnapshot.getValue(Message.class);
                UIMessage uiMessage = MessagesHelper.convertFromDbMessageModelToUIMessage(message,
                                                                                          loggedInProfile,
                                                                                          messageToProfile);
                messagesListAdapter.addToStart(uiMessage, true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // do nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // do nothing
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // do nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // do nothing
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    private void setupPrecannedMessage() {
        Similarities similarities = SimilaritiesFinder.findSimilarities(loggedInProfile, messageToProfile);
        if (similarities.numOfSimilarities != 0) {
            if (similarities.numOfSimilarities > 1) {
                setMessage(String.format(getString(R.string.message_things_in_common), messageToProfile.firstName, String.valueOf(
                    similarities.numOfSimilarities
                )));
            } else {
                if (similarities.similarEducations != null && similarities.similarEducations.size() != 0) {
                    setMessage(String.format(getString(R.string.message_also_studied_at), messageToProfile.firstName, similarities.similarEducations.get(0).schoolName));
                } else if (similarities.similarPositions != null && similarities.similarPositions.size() != 0) {
                    setMessage(String.format(getString(R.string.message_also_worked_at), messageToProfile.firstName, similarities.similarPositions.get(0).companyName));
                }
            }
        }
    }

    private void setMessage(String message) {
        messageInput.getInputEditText().setText(message);
    }

    private void resetPrecannedMessage() {
        resetMessage = true;
        messageInput.getInputEditText().setText("");
    }

    // Please ignore the section below, kept for testing
    /**
     * If database does not exists, then create the database,
     * if database exists then fetch the list of messages
     */
//    private void bootstrapMessagesFromDatabase() {
//        Database.instance().messagesTableReference().child(messagesRowId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<UIMessage> bootstrappedUIMessages = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Message message = snapshot.getValue(Message.class);
//                    // adding at position 0 since these come in reserve chron order
//                    bootstrappedUIMessages.add(0,
//                                               MessagesHelper.convertFromDbMessageModelToUIMessage(message,
//                                                                                                   loggedInProfile,
//                                                                                                   messageToProfile));
//                }
//                if (bootstrappedUIMessages.size() > 0) {
//                    messagesListAdapter.addToEnd(bootstrappedUIMessages, false);
//                    messagesListAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
