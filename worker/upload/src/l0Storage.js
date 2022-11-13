/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

const url = 'https://storage.l0.mytiki.com/api/latest/report'
const jwtAlg = {
  name: 'ECDSA',
  namedCurve: 'P-256',
  hash: 'SHA-256'
}
const reqClaims = ['iss', 'iat', 'sub', 'jti', 'exp']
const iss = 'com.mytiki.l0_storage'
const clockSkewInMinutes = 5

export { report, decode, guardClaims }

async function report (clientId, clientSecret, path, sizeBytes) {
  return fetch(
    new Request(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Basic ' + btoa(clientId + ':' + clientSecret)
      },
      body: JSON.stringify({ path, sizeBytes })
    })
  )
}

async function decode (jwt, pubKey) {
  const split = jwt.split('.')
  const headerB64 = split[0]
  const payloadB64 = split[1]
  const signatureB64 = split[2]

  const cryptoKey = await crypto.subtle.importKey(
    'jwk', pubKey, jwtAlg, false, ['verify'])

  const isValid = await crypto.subtle.verify(jwtAlg, cryptoKey,
    b64UrlDecode(signatureB64), new TextEncoder().encode([headerB64, payloadB64].join('.')))

  if (!isValid) { throw new Error('Failed to validate JWT') }
  return JSON.parse(new TextDecoder().decode(b64UrlDecode(payloadB64)))
}

function guardClaims (claims) {
  reqClaims.forEach((claim, i) => {
    if (claims[claim] == null) { throw new Error('Missing required claim: ' + claim) }
  })

  if (claims.iss !== iss) { throw new Error('Invalid ISS claim') }

  const iatDate = new Date(claims.iat * 1000)
  const expDate = new Date((claims.exp * 1000) + (clockSkewInMinutes * 60 * 1000))

  if (iatDate >= new Date()) throw new Error('Invalid IAT claim')
  if (expDate < new Date()) throw new Error('Invalid EXP claim')
}

// from https://thewoods.blog/base64url/
function b64UrlDecode (value) {
  const m = value.length % 4
  return Uint8Array.from(atob(
    value.replace(/-/g, '+')
      .replace(/_/g, '/')
      .padEnd(value.length + (m === 0 ? 0 : 4 - m), '=')
  ), c => c.charCodeAt(0)).buffer
}
