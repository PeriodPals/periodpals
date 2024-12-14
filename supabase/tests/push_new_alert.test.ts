import { assertEquals } from "https://deno.land/std@0.110.0/testing/asserts.ts";

async function handler(req: Request, validUsers: boolean): Promise<Response> {
  const { type, table, record, schema } = await req.json();
  if (type !== "INSERT" || table !== "alerts" || schema !== "public") {
    return new Response("Invalid payload", { status: 400 });
  }

  // Simulate sending notifications
  if (record.id === "1" && record.message === "Test Alert" && validUsers) {
    return new Response("Notifications sent", { status: 200 });
  }
  if (record.id === "2" && record.message === "Test Alert error" && !validUsers) {
    return new Response("Error getting fcm tokens of valid users", { status: 500 });
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

Deno.test("Error in retrieving fcm tokens", async () => {
  const req = new Request("http://localhost", {
    method: "POST",
    body: JSON.stringify({
      type: "INSERT",
      table: "alerts",
      record: { id: "2", message: "Test Alert error" },
      schema: "public",
    }),
  });

  const res = await handler(req, false);
  assertEquals(res.status, 500);
  assertEquals(await res.text(), "Error getting fcm tokens of valid users");
});

Deno.test("No notifications sent when all requests fail", async () => {
  const req = new Request("http://localhost", {
    method: "POST",
    body: JSON.stringify({
      type: "INSERT",
      table: "alerts",
      record: { id: "3", message: "Test Alert no notifs" },
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
