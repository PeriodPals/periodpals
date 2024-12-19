import { createClient } from "npm:@supabase/supabase-js@2";
import { JWT } from "npm:google-auth-library@9";
import serviceAccount from "../service-account.json" with { type: "json" };

/**
 * Represents a user with a unique identifier.
 *
 * @interface User
 * @property {string} user_id - The unique identifier for the user from the `public.users` table.
 */
interface User {
  user_id: string;
}

/**
 * Represents the payload sent to a webhook when a user is deleted.
 *
 * @interface WebhookPayload
 * @property {"DELETE"} type - The type of operation that triggered the webhook. Always "DELETE" for user deletions.
 * @property {string} table - The name of the table where the user was deleted.
 * @property {User} old_record - The previous record that was deleted.
 * @property {"public"} schema - The schema of the table where the user was deleted. Always "public".
 */
interface WebhookPayload {
  type: "DELETE";
  table: string;
  old_record: User;
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

  // check if payload.old_record is not null or undefined
  if (!payload.old_record || !payload.old_record.user_id) {
    console.error("Invalid payload:", payload);
    return new Response('Invalid payload', { status: 400 });
  }

  // delete user from `auth.users` using database function
  const { data, error } = await supabase
    .rpc("delete_auth_user_with_uid", { input_id: payload.old_record.user_id });

  if (error) {
    console.error("Error deleting user:", error);
    return new Response('Error deleting user', { status: 500 });
  }
  return new Response('User deleted', { status: 200 });
});

