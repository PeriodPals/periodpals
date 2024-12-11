import { createClient } from 'npm:@supabase/supabase-js@2';
import { JWT } from 'npm:google-auth-library@9';
import serviceAccount from '../service-account.json' with { type: 'json' };


/**
 * Represents a timer with a unique identifier, a unique identifier for the user it belongs to,
 * and a message to be sent.
 *
 * @interface Timer
 * @property {string} id - The unique identifier for the timer.
 * @property {string} user_id - The unique identifier for the owner of the timer.
 * @property {string} instructionText - The message content of the timer.
 */
interface Timer {
  id: string;
  user_id: string;
  instructionText: string;
}

/**
 * Represents the payload sent to a webhook when a timer is updated.
 *
 * @interface WebhookPayload
 * @property {"INSERT"} type - The type of operation that triggered the webhook. Always "UPDATE" for timers.
 * @property {string} table - The name of the table where the timers are updated.
 * @property {Alert} record - The timer record or the updated version.
 * @property {"public"} schema - The schema of the table where the timer was updated. Always "public".
 */
interface WebhookPayload {
  type: "UPDATE";
  table: string;
  record: Timer;
  schema: 'public';
}


// supabase client with service account access (admin access)
const supabase = createClient(
  Deno.env.get('SUPABASE_URL')!,
  Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
);

// create a server to listen for new alerts
Deno.serve(async (req) => {
  // get the webhook payload
  const payload: WebhookPayload = await req.json();

  // check if the instructionText is null
  if (payload.record.instructionText === null) {
    console.log(`No notification to send for timer ${payload.record.id}`);
    return new Response('No notification to send', { status: 200 });
  }

  // get the fcm token for the user owner of the timer using database function
  const { data: fcmToken, error } = await supabase
    .rpc("get_user_token_from_timer", { timer_id: payload.record.id });
  if (error || !fcmToken) {
    console.log(`No FCM token found for timer ${payload.record.id}`);
    return new Response('Error fetching FCM token', { status: 500 });
  }

  // get access token for sending notifications
  const accessToken = await getAccessToken({
    clientEmail: serviceAccount.client_email,
    privateKey: serviceAccount.private_key,
  });

  // send notification to the user
  const res = await fetch(
    `https://fcm.googleapis.com/v1/projects/${serviceAccount.project_id}/messages:send`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({
        message: {
          token: fcmToken[0]["fcm_token"],
          notification: {
            title: `Your Timer`,
            body: payload.record.instructionText,
          },
        },
      }),
    }
  );

  const resData = await res.json();
  if (res.status < 200 || 299 < res.status) {
    console.error(`No notification sent: ${JSON.stringify(resData)}`);
    return new Response('No notification sent', { status: 500 });
  }

  return new Response('Notification sent', { status: 200 });
})

const getAccessToken = ({ clientEmail, privateKey }: {
  clientEmail: string;
  privateKey: string;
}): Promise<string> => {
  return new Promise((resolve, reject) => {
    const jwtClient = new JWT({
      email: clientEmail,
      key: privateKey,
      scopes: ['https://www.googleapis.com/auth/firebase.messaging'],
    });
    jwtClient.authorize((err, tokens) => {
      if (err) {
        reject(err);
        return;
      }
      resolve(tokens!.access_token!);
    });
  });
};

