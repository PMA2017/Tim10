package com.example.pma_tim10.chatapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.pma_tim10.chatapp.ChatApp;
import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MainActivity;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.SharedPrefUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Daniel on 6/10/2017.
 */

public class ChatMessagingService extends FirebaseMessagingService {

    private static final String TAG = ChatMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("username");
            String uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            String conversationid = remoteMessage.getData().get("conversation_id");
            String conversationName = remoteMessage.getData().get("converastion_name");

            // save unseen conversations
            SharedPrefUtil pref = new SharedPrefUtil(getApplicationContext());
            Set<String> unseenConversations = pref.getStringSet(Constants.CONVERSATION_NEW_MESSAGES_SET, new HashSet<String>());
            unseenConversations.add(conversationid);
            pref.saveStringSet(Constants.CONVERSATION_NEW_MESSAGES_SET,unseenConversations);



            // Don't show notification if chat activity is open.
            if (!ChatApp.isChatActivityOpen()) {
                sendNotification(title,
                        message,
                        username,
                        uid,
                        conversationid,
                        conversationName,
                        fcmToken);
            } else {
//                EventBus.getDefault().post(new PushNotificationEvent(title,
//                        message,
//                        username,
//                        uid,
//                        fcmToken));
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String conversationId,
                                  String conversationName,
                                  String firebaseToken) {

        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(Constants.IE_CONVERSATION_ID_KEY, conversationId);
        intent.putExtra(Constants.IE_USER_ID_KEY, receiverUid);
        intent.putExtra(Constants.IE_CONVERSATION_NAME, conversationName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.chat_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
