import { createClient } from "npm:@supabase/supabase-js@2";
import { JWT } from "npm:google-auth-library@9";
import serviceAccount from "../service-account.json" with { type: "json" };

interface Alert {
  id: string;
  message: string;
  //   user_id: string
  //   body: string
}

interface WebhookPayload {
  type: "INSERT";
  table: string;
  record: Alert;
  schema: "public";
}

const supabase = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

Deno.serve(async (req) => {
  const payload: WebhookPayload = await req.json();

  const { data: users } = await supabase
    .from("users")
    .select("fcm_token");

  const validUsers = users!.filter(user => user.fcm_token !== null);

  const accessToken = await getAccessToken({
    clientEmail: serviceAccount.client_email,
    privateKey: serviceAccount.private_key,
  });

  const notifications = validUsers.map((user) => {
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
            token: user.fcm_token,
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
  const successful = res.filter(res => res.status > 200 && 299 > res.status)
  if (successful.length < 1) {
    return new Response('No notifications were sent', { status: 500 })
  }

  return new Response('Notifications sent', { status: 200 });
});

const getAccessToken = ({
  clientEmail,
  privateKey,
}: {
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
