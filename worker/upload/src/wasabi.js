/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

const enc = new TextEncoder()

export {
  put,
  canonicalRequest,
  signingKey,
  stringToSign,
  buf2hex,
  authorization,
  date2timestamp,
  date2datestamp,
  hmacSha
}

async function put (key, req, config) {
  if (!req.key.startsWith('/')) req.key = '/' + req.key
  const date = new Date()
  const hashedPayload = buf2hex(await crypto.subtle.digest('SHA-256', req.file))
  const signedHeaders = 'host;x-amz-content-sha256;x-amz-date'
  const canonicalHeaders = 'host:' + config.bucket + '\n' +
      'x-amz-content-sha256:' + hashedPayload + '\n' +
      'x-amz-date:' + date2timestamp(date) + '\n'
  const cReq = await canonicalRequest('PUT', req.key, undefined,
    canonicalHeaders, signedHeaders, hashedPayload)
  const s2s = await stringToSign(date, config.region, config.service, cReq)
  const signKey = await signingKey(key.secret, date, config.region, config.service)
  const signature = buf2hex(await hmacSha(enc.encode(s2s), signKey))
  const auth = authorization(key.id, date, config.region, config.service, signedHeaders, signature)

  return fetch('https://' + config.bucket + req.key, {
    method: 'PUT',
    headers: {
      Authorization: auth,
      'x-amz-date': date2timestamp(date),
      'x-amz-content-sha256': hashedPayload
    },
    body: req.file
  })
}

function canonicalRequest (httpMethod, canonicalUri, canonicalQueryString, canonicalHeaders, signedHeaders, hashedPayload) {
  return [httpMethod, canonicalUri, canonicalQueryString, canonicalHeaders, signedHeaders, hashedPayload].join('\n')
}

async function stringToSign (date, region, service, canonicalRequest) {
  const scope = [date2datestamp(date), region, service, 'aws4_request'].join('/')
  const hashedCanonical = await crypto.subtle.digest('SHA-256', enc.encode(canonicalRequest))
  return 'AWS4-HMAC-SHA256' + '\n' +
      date2timestamp(date) + '\n' +
      scope + '\n' +
      buf2hex(hashedCanonical)
}

async function signingKey (secretKey, date, region, service) {
  const kDate = await hmacSha(enc.encode(date2datestamp(date)), enc.encode('AWS4' + secretKey))
  const kRegion = await hmacSha(enc.encode(region), kDate)
  const kService = await hmacSha(enc.encode(service), kRegion)
  return await hmacSha(enc.encode('aws4_request'), kService)
}

function authorization (keyId, date, region, service, signedHeaders, signature) {
  const credential = [keyId, date2datestamp(date), region, service, 'aws4_request'].join('/')
  return ['AWS4-HMAC-SHA256 Credential=' + credential,
    'SignedHeaders=' + signedHeaders,
    'Signature=' + signature
  ].join(',')
}

// from: https://stackoverflow.com/questions/47329132/how-to-get-hmac-with-crypto-web-api
async function hmacSha (body, key) {
  const algorithm = { name: 'HMAC', hash: 'SHA-256' }
  const cryptoKey = await crypto.subtle.importKey('raw', key, algorithm, false, ['sign'])
  return await crypto.subtle.sign(algorithm.name, cryptoKey, body)
}

// from: https://stackoverflow.com/questions/40031688/javascript-arraybuffer-to-hex
function buf2hex (buffer) {
  return [...new Uint8Array(buffer)]
    .map(b => b.toString(16).padStart(2, '0'))
    .join('')
}

function date2datestamp (date) {
  return [
    date.getUTCFullYear(),
    (date.getUTCMonth() + 1).toString().padStart(2, '0'),
    (date.getUTCDate()).toString().padStart(2, '0')
  ].join('')
}

function date2timestamp (date) {
  return [
    date.getUTCFullYear(),
    (date.getUTCMonth() + 1).toString().padStart(2, '0'),
    (date.getUTCDate()).toString().padStart(2, '0'),
    'T',
    (date.getUTCHours()).toString().padStart(2, '0'),
    (date.getUTCMinutes()).toString().padStart(2, '0'),
    (date.getUTCSeconds()).toString().padStart(2, '0'),
    'Z'
  ].join('')
}
