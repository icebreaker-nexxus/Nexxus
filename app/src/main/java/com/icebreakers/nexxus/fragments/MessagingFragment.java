package com.icebreakers.nexxus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.helpers.MessagesHelper;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.models.Message;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.messaging.MessageRef;
import com.icebreakers.nexxus.models.messaging.UIMessage;
import com.icebreakers.nexxus.persistence.Database;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amodi on 4/12/17.
 */

public class MessagingFragment extends Fragment {

    private static final String PROFILE_EXTRA = "profile_extra";

    @BindView(R.id.messagesList) MessagesList messagesList;
    @BindView(R.id.input) MessageInput messageInput;

    MessagesListAdapter<UIMessage> messagesListAdapter;
    Profile loggedInProfile;
    Profile messageToProfile;
    String messagesRowId;

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
        messageToProfile = Parcels.unwrap(getArguments().getParcelable(PROFILE_EXTRA));
        loggedInProfile = ProfileHolder.getInstance(getContext()).getProfile();
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
        //bootstrapMessagesFromDatabase();
        return view;
    }

    private void setupInputMessageListener() {
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence charSequence) {
                Message message = new Message();
                message.timestamp = Calendar.getInstance().getTimeInMillis();
                message.text = charSequence.toString();
                message.id = UUID.randomUUID().toString();
                message.senderId = loggedInProfile.id;
                Database.instance().saveMessage(messagesRowId, message);
                Database.instance().saveMessageToProfile(loggedInProfile, new MessageRef(messagesRowId, messageToProfile.id));
                return true;
            }
        });
    }


    private void setupNewIncomingMessageListener() {
        Database.instance().messagesTableReference().child(messagesRowId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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
