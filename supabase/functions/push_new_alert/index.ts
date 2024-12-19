import { createClient } from "npm:@supabase/supabase-js@2";
import { JWT } from "npm:google-auth-library@9";
import serviceAccount from "../service-account.json" with { type: "json" };


/**
 * Represents an alert with a unique identifier, message, and GIS location.
 * 
 * @interface Alert
 * @property {string} id - The unique identifier for the alert.
 * @property {string} message - The message content of the alert.
 * @property {string} locationGIS - The GIS location associated with the alert.
 */
interface Alert {
  id: string;
  message: string;
  locationGIS: string;
}

/**
 * Represents the payload sent to a webhook when a new alert is inserted.
 *
 * @interface WebhookPayload
 * @property {"INSERT"} type - The type of operation that triggered the webhook. Always "INSERT" for new alerts.
 * @property {string} table - The name of the table where the new alert was inserted.
 * @property {Alert} record - The new alert record that was inserted.
 * @property {"public"} schema - The schema of the table where the new alert was inserted. Always "public".
 */
interface WebhookPayload {
  type: "INSERT";
  table: string;
  record: Alert;
  schema: "public";
}

// supabase client with service account access (admin access)
const supabase = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

// create a server to listen for new alerts
Deno.serve(async (req) => {
  // get the webhook payload
  const payload: WebhookPayload = await req.json();

  // get fcm token of valid users from supabase using database function
  const { data: fcmTokens, error } = await supabase
    .rpc("get_tokens_of_users_within_alert_distance", { alert_id: payload.record.id });

  if (error) {
    console.error("Error getting fcm tokens of valid users:", error);
    return new Response('Error getting fcm tokens of valid users', { status: 500 });
  }
  console.log(`Number of fcm tokens: ${fcmTokens.length}`);

  // get access token for sending notifications
  const accessToken = await getAccessToken({
    clientEmail: serviceAccount.client_email,
    privateKey: serviceAccount.private_key,
  });

  // send notifications to all valid users
  const notifications = fcmTokens.map((fcmToken) => {
    return fetch(
      `https://fcm.googleapis.com/v1/projects/${serviceAccount.project_id}/messages:send`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({
          message: {
            token: fcmToken["fcm_token"],
            notification: {
              title: "New Alert",
              body: payload.record.message,
            },
          },
        }),
      },
    );
  });

  const res = await Promise.all(notifications)
  const successful = res.filter(res => res.status >= 200 && 299 >= res.status)
  if (successful.length < 1) {
    console.error('No notifications were sent');
    return new Response('No notifications were sent', { status: 500 })
  }
  console.log('Notifications sent');
  return new Response('Notifications sent', { status: 200 });
});


/**
 * Get an access token for the Firebase Cloud Messaging API.
 * 
 * @param {string} clientEmail - The client email of the service account.
 * @param {string} privateKey - The private key of the service account.
 * @returns {Promise<string>} - The access token.
 */
const getAccessToken = ({ clientEmail, privateKey }: {
  clientEmail: string;
  privateKey: string;
}): Promise<string> => {
  return new Promise((resolve, reject) => {
    const jwtClient = new JWT({
      email: clientEmail,
      key: privateKey,
      scopes: ["https://www.googleapis.com/auth/firebase.messaging"],
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
