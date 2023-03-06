/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as b64 from './b64.js'

export { report, decode, guardClaims }

async function report (url, key, body) {
  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(`${key.id}:${key.secret}`)
    },
    body: JSON.stringify(body)
  })
}

async function decode (jwt, pubKey, jwtAlg) {
  const split = jwt.split('.')
  const headerB64 = split[0]
  const payloadB64 = split[1]
  const signatureB64 = split[2]

  const cryptoKey = await crypto.subtle.importKey(
    'jwk', pubKey, jwtAlg, false, ['verify'])

  const isValid = await crypto.subtle.verify(jwtAlg, cryptoKey,
    b64.decode(signatureB64, true), new TextEncoder().encode([headerB64, payloadB64].join('.')))

  if (!isValid) { throw new Error('Failed to validate JWT') }
  return JSON.parse(new TextDecoder().decode(b64.decode(payloadB64, true)))
}

function guardClaims (req, config) {
  const reqClaims = config.claims.split(',')
  reqClaims.forEach((claim, i) => {
    if (req[claim] == null) { throw new Error('Missing required claim: ' + claim) }
  })

  if (req.iss !== config.iss) { throw new Error('Invalid ISS claim') }

  const iatDate = new Date(req.iat * 1000)
  const expDate = new Date((req.exp * 1000) + (config.clockSkew * 60 * 1000))

  if (iatDate >= new Date()) throw new Error('Invalid IAT claim')
  if (expDate < new Date()) throw new Error('Invalid EXP claim')
}
