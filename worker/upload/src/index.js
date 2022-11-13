/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import { decode, guardClaims } from './l0Storage.js'
import { put } from './wasabi.js'

export default {
  async fetch (request, env, context) {
    try {
      handleMethod(request)
      const body = await handleBody(request, env)
      await handleAuth(request, env, body)

      const wasabiRsp = await put(env.WASABI_ID, env.WASABI_SECRET, body.key, new TextEncoder().encode(body.block))
      if (wasabiRsp.status !== 200) {
        return Response.json({
          message: 'Bucket upload failed',
          help: 'Contact support'
        }, { status: 424 })
      }

      return new Response('', { status: 201, headers: { 'Content-Type': 'application/json' } })
    } catch (error) {
      if (error instanceof Response) return error
      else {
        Response.json({
          message: error.toString()
        }, { status: 500 })
      }
    }
  }

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

function handleMethod (request) {
  if (request.method !== 'PUT') { throw Response.json({ message: 'Not Allowed' }, { status: 405 }) }
}

async function handleBody (request, env) {
  let body
  try {
    body = await request.json()
  } catch (error) {
    throw Response.json({ message: 'Malformed body' }, { status: 400 })
  }
  if (body.key == null || body.block == null) {
    throw Response.json({
      message: 'Missing required parameter',
      detail: 'Both key & block are required'
    }, { status: 400 })
  }
  if (body.block.length > env.MAX_BYTES) {
    throw Response.json({
      message: 'Request too large',
      detail: 'Max block size is 1MB'
    }, { status: 413 })
  }
  return {
    key: body.key,
    block: atob(body.block)
  }
}

async function handleAuth (request, env, body) {
  let claims
  try {
    const token = request.headers.get('authorization').replace('Bearer ', '')
    claims = await decode(token, JSON.parse(env.PUBKEY))
    guardClaims(claims)
  } catch (error) {
    throw Response.json({
      message: 'Failed to authorize request',
      detail: 'A valid bearer token is required',
      help: 'Request a valid token from api/latest/token'
    }, { status: 401 })
  }
  if (!body.key.startsWith(claims.sub)) {
    throw Response.json({
      message: 'Failed to authorize request',
      detail: 'Request key out of token scope',
      help: 'Key must fit under sub claim route'
    }, { status: 401 })
  }
}
