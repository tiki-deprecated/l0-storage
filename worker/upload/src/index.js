/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as l0Storage from './l0Storage.js'
import * as wasabi from './wasabi.js'
import * as b64 from './b64.js'
import * as l0Index from './l0Index.js'

export default {
  async fetch (request, env, context) {
    try {
      handleMethod(request)
      const body = await handleBody(request, env)
      await handleAuth(request, env, body)
      const contentBytes = b64.decode(body.content)

      const wasabiRsp = await wasabi.put(
        { id: env.WASABI_ID, secret: env.WASABI_SECRET },
        { key: body.key, file: contentBytes },
        { bucket: env.WASABI_BUCKET, region: env.WASABI_REGION, service: env.WASABI_SERVICE })
        .catch((error) => {
          console.log(error)
        })
      const versionId = wasabiRsp.headers.get('x-amz-version-id')
      if (wasabiRsp.status !== 200) {
        return Response.json({
          message: 'Bucket upload failed',
          help: 'Contact support'
        }, { status: 424 })
      }

      const l0StorageRsp = await l0Storage.report(
        env.L0_STORAGE_URL,
        { id: env.REMOTE_ID, secret: env.REMOTE_SECRET },
        { path: body.key, sizeBytes: contentBytes.byteLength })
        .catch((error) => {
          console.log(error)
        })
      if (l0StorageRsp.status !== 204) {
        console.log('WARNING. Failed to report usage')
        console.log(await l0StorageRsp.json())
      }

      if (body.key.endsWith('.block')) {
        const l0IndexRsp = await l0Index.report(
          { id: env.INDEX_ID, secret: env.INDEX_SECRET },
          { path: body.key, block: contentBytes, version: versionId },
          { bucket: env.L0_INDEX_BUCKET, url: env.L0_INDEX_URL })
          .catch((error) => {
            console.log(error)
          })
        if (l0IndexRsp.status !== 204) {
          console.log('WARNING. Failed to report index')
          console.log(await l0IndexRsp.json())
        }
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
  if (request.method !== 'PUT') {
    throw Response.json(
      { message: 'Not Allowed' },
      { status: 405 })
  }
}

async function handleBody (request, env) {
  let body
  try {
    body = await request.json()
  } catch (error) {
    throw Response.json({ message: 'Malformed body' }, { status: 400 })
  }
  if (body.key == null || body.content == null) {
    throw Response.json({
      message: 'Missing required parameter',
      detail: 'Both key & content are required'
    }, { status: 400 })
  }
  if (body.content.length > env.MAX_BYTES) {
    throw Response.json({
      message: 'Request too large',
      detail: 'Max content size is 1MB'
    }, { status: 413 })
  }
  return body
}

async function handleAuth (request, env, body) {
  let claims
  try {
    const token = request.headers.get('authorization').replace('Bearer ', '')
    claims = await l0Storage.decode(token, JSON.parse(env.L0_STORAGE_JWT_JWKS), {
      name: env.L0_STORAGE_JWT_ALG,
      namedCurve: env.L0_STORAGE_JWT_CRV,
      hash: env.L0_STORAGE_JWT_HASH
    })
    l0Storage.guardClaims(claims, {
      claims: env.L0_STORAGE_JWT_CLAIMS,
      iss: env.L0_STORAGE_JWT_ISS,
      clockSkew: env.CLOCK_SKEW_MINUTES
    })
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
