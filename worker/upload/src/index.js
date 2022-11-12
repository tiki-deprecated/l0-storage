/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import { test } from './wasabi.js'

export default {
  async fetch (request, env, context) {
    await test()
    return new Response('Hello World!')

  /*
    if (request.method === 'POST') {
      const requestBody = await request.clone().formData()
      const wasabiResponse = await fetch(
        new Request('https://' + env.BUCKET_NAME, {
          method: request.method,
          headers: request.headers,
          body: request.body
        })
      )
      if (wasabiResponse.status !== 204) return wasabiResponse
      else {
        const file = requestBody.get('file')
        return report(
          env.REMOTE_ID,
          env.REMOTE_SECRET,
          requestBody.get('key'),
          file.length == null ? file.size : file.length
        )
      }
    } else {
      return new Response(
        JSON.stringify({
          message: 'Full authentication is required to access this resource'
        }),
        {
          status: 401,
          statusText: 'Unauthorized',
          headers: { 'Content-Type': 'application/json' }
        }
      )
    } */
  }
}
