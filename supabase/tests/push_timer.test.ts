import { assertEquals } from 'https://deno.land/std@0.110.0/testing/asserts.ts';
import { createClient } from 'npm:@supabase/supabase-js@2';
import { JWT } from 'npm:google-auth-library@9';
import serviceAccount from '../functions/service-account.json' with { type: 'json' };

/**
 * Handler representing the `push_timer` function. Used only for testing purposes.
 * //TODO: This should always be up-to-date with how the function is implemented.
 */
async function handler(
  req: Request,
  data: any,
  dataError: Error | null,
  res: { status: number },
): Promise<Response> {
  const { type, table, record, schema } = await req.json();

  if (type !== "UPDATE" || table !== "timers" || schema !== "public") {
    return new Response("Invalid payload", { status: 400 });
  }

  if (record.instructionText === null) {
    return new Response('No notification to send', { status: 200 });
  }
  if (dataError || data === null) {
    return new Response('Error fetching FCM token', { status: 500 });
  }
  if (res.status < 200 || 299 < res.status) {
    return new Response('No notification sent', { status: 500 });
  }
  return new Response('Notification sent', { status: 200 });
}

Deno.test('push_timer does not send notification when instructionText is null', async () => {
  const req = new Request('http://localhost', {
    method: 'POST',
    body: JSON.stringify({
      type: 'UPDATE',
      table: 'timers',
      record: {
        id: 'timer1',
        user_id: 'user1',
        instructionText: null,
      },
      schema: 'public',
    }),
  });
  const data = [{ fcm_token: 'fcm token' }];
  const error = null;
  const res = { status: 200 };

  const expected = new Response('No notification to send', { status: 200 });
  const actual = await handler(req, data, error, res);
  assertEquals(actual.status, expected.status);
  assertEquals(await actual.text(), await expected.text());
});

Deno.test('push_timer sends notification when instructionText is not null', async () => {
  const req = new Request('http://localhost', {
    method: 'POST',
    body: JSON.stringify({
      type: 'UPDATE',
      table: 'timers',
      record: {
        id: 'timer1',
        user_id: 'user1',
        instructionText: 'Test notification',
      },
      schema: 'public',
    }),
  });
  const data = [{ fcm_token: 'fcm token' }];
  const error = null;
  const res = { status: 200 };

  const expected = new Response('Notification sent', { status: 200 });
  const actual = await handler(req, data, error, res);
  assertEquals(actual.status, expected.status);
  assertEquals(await actual.text(), await expected.text());
});

Deno.test('push_timer returns error when FCM token is not found', async () => {
  const req1 = new Request('http://localhost', {
    method: 'POST',
    body: JSON.stringify({
      type: 'UPDATE',
      table: 'timers',
      record: {
        id: 'timer1',
        user_id: 'user1',
        instructionText: 'Test notification',
      },
      schema: 'public',
    }),
  });
  const req2 = req1.clone();
  const data1: any = null;
  const error1 = null;
  const res = { status: 200 };

  const expected1 = new Response('Error fetching FCM token', { status: 500 });
  const expected2 = expected1.clone();
  const actual1 = await handler(req1, data1, error1, res);
  assertEquals(actual1.status, expected1.status);
  assertEquals(await actual1.text(), await expected1.text());

  const data2 = [{ fcm_token: 'fcm token' }];
  const error2 = new Error('Failed to fetch FCM token');

  const actual2 = await handler(req2, data2, error2, res);
  assertEquals(actual2.status, expected2.status);
  assertEquals(await actual2.text(), await expected2.text());
});

Deno.test('push_timer returns error when notification fails to send', async () => {
  const req = new Request('http://localhost', {
    method: 'POST',
    body: JSON.stringify({
      type: 'UPDATE',
      table: 'timers',
      record: {
        id: 'timer1',
        user_id: 'user1',
        instructionText: 'Test notification',
      },
      schema: 'public',
    }),
  });
  const data = [{ fcm_token: 'fcm token' }];
  const error = null;
  const res = { status: 500 };

  const expected = new Response('No notification sent', { status: 500 });
  const actual = await handler(req, data, error, res);
  assertEquals(actual.status, expected.status);
  assertEquals(await actual.text(), await expected.text());
});

Deno.test('push_timer returns error when payload is invalid', async () => {
  const req = new Request('http://localhost', {
    method: 'POST',
    body: JSON.stringify({
      type: 'UPDATE',
      table: 'timers',
      record: {
        id: 'timer1',
        user_id: 'user1',
        instructionText: 'Test notification',
      },
      schema: 'invalid',
    }),
  });
  const data = [{ fcm_token: 'fcm token' }];
  const error = null;
  const res = { status: 200 };

  const expected = new Response('Invalid payload', { status: 400 });
  const actual = await handler(req, data, error, res);
  assertEquals(actual.status, expected.status);
  assertEquals(await actual.text(), await expected.text());
});
