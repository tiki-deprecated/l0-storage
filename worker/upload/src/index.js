/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

export default {
  async fetch(request, env, context) {
    if (request.method === "POST") {
      const requestBody = await request.clone().formData();
      const wasabiResponse = await fetch(
        new Request("https://" + env.BUCKET_NAME, {
          method: request.method,
          headers: request.headers,
          body: request.body,
        })
      );
      if (wasabiResponse.status !== 204) return wasabiResponse;
      else {
        return fetch(
          new Request("https://storage.l0.mytiki.com/api/latest/usage", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization:
                "Basic " + btoa(env.REMOTE_ID + ":" + env.REMOTE_SECRET),
            },
            body: JSON.stringify({
              path: requestBody.get("key"),
              sizeBytes: requestBody.get("file").size,
            }),
          })
        );
      }
    } else {
      return new Response(
        JSON.stringify({
          message: "Full authentication is required to access this resource",
        }),
        {
          status: 401,
          statusText: "Unauthorized",
          headers: { "Content-Type": "application/json" },
        }
      );
    }
  },
};
