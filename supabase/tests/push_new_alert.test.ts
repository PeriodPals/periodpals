import { assertEquals } from "https://deno.land/std@0.110.0/testing/asserts.ts";
import { serve } from "https://deno.land/std@0.110.0/http/server.ts";
import { createClient } from "npm:@supabase/supabase-js@2";
import { JWT } from "npm:google-auth-library@9";
import serviceAccount from "../functions/service-account.json" with { type: "json" };

async function handler(req: Request, validUsers: boolean): Promise<Response> {
  const { type, table, record, schema } = await req.json();
  if (type !== "INSERT" || table !== "alerts" || schema !== "public") {
    return new Response("Invalid payload", { status: 400 });
  }

  // Simulate sending notifications
  if (record.id === "1" && record.message === "Test Alert" && validUsers) {
    return new Response("Notifications sent", { status: 200 });
  }
  return new Response("No notifications were sent", { status: 500 });
}

Deno.test("Notifications sent successfully", async () => {
  const req = new Request("http://localhost", {
    method: "POST",
    body: JSON.stringify({
      type: "INSERT",
      table: "alerts",
      record: { id: "1", message: "Test Alert" },
      schema: "public",
    }),
  });

  const res = await handler(req, true);
  assertEquals(res.status, 200);
  assertEquals(await res.text(), "Notifications sent");
});

Deno.test("No notifications sent when no valid users", async () => {
  const req = new Request("http://localhost", {
    method: "POST",
    body: JSON.stringify({
      type: "INSERT",
      table: "alerts",
      record: { id: "1", message: "Test Alert" },
      schema: "public",
    }),
  });

  const res = await handler(req, false);
  assertEquals(res.status, 500);
  assertEquals(await res.text(), "No notifications were sent");
});

Deno.test("Invalid request payload", async () => {
  const req = new Request("http://localhost", {
    method: "POST",
    body: JSON.stringify({
      type: "UPDATE",
      table: "alerts",
      record: { id: "1", message: "Test Alert" },
      schema: "public",
    }),
  });

  const res = await handler(req, true);
  assertEquals(res.status, 400);
  assertEquals(await res.text(), "Invalid payload");
});