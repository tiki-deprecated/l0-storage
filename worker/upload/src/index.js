/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import { decode, guardClaims, report } from './l0Storage.js'
import { put } from './wasabi.js'

export default {
  async fetch (request, env, context) {
    try {
      handleMethod(request)
      const body = await handleBody(request, env)
      await handleAuth(request, env, body)

      const blockBytes = new TextEncoder().encode(atob(body.block))
      const wasabiRsp = await put(env.WASABI_ID, env.WASABI_SECRET, body.key, blockBytes)
      if (wasabiRsp.status !== 200) {
        return Response.json({
          message: 'Bucket upload failed',
          help: 'Contact support'
        }, { status: 424 })
      }

      const l0Rsp = await report(env.REMOTE_ID, env.REMOTE_SECRET, body.key, blockBytes.length)
      if (l0Rsp.status !== 200) {
        console.log('WARNING. Failed to report usage')
        console.log(l0Rsp)
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
  return body
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
