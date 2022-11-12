/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

const enc = new TextEncoder()

export async function put (bucket, key, file) {
  return await fetch(
    new Request('https://' + bucket, {
      method: 'PUT'
      // headers: request.headers,
      // body: request.body
    })
  )
}

const testFile = '{ "hello" : "world" }'

export async function test () {
  const keyId = ''
  const secretKey = ''
  const date = new Date()
  console.log('x-amz-date: ' + date2timestamp(date))

  const hashedPayload = buf2hex(await crypto.subtle.digest('SHA-256', enc.encode(testFile)))
  console.log('x-amz-content-sha256: ' + hashedPayload)

  const region = 'us-central-1'
  const service = 's3'
  const signedHeaders = 'content-type;host;x-amz-content-sha256;x-amz-date'
  const canonicalHeaders = 'content-type:application/json\n' +
      'host:bucket.storage.l0.mytiki.com\n' +
      'x-amz-content-sha256:' + hashedPayload + '\n' +
      'x-amz-date:' + date2timestamp(date) + '\n'

  const cReq = await canonicalRequest('PUT', '/test.json', undefined,
    canonicalHeaders, signedHeaders, hashedPayload)

  console.log(cReq)

  const s2s = await stringToSign(date, region, service, cReq)
  const signKey = await signingKey(secretKey, date, region, service)
  const signature = buf2hex(await hmacSha(enc.encode(s2s), signKey))
  const auth = authorization(keyId, date, region, service, signedHeaders, signature)
  console.log('Authorization: ' + auth)

  await fetch(
    new Request('https://bucket.storage.l0.mytiki.com/test.json', {
      method: 'PUT',
      headers: {
        Authorization: auth,
        'Content-Type': 'application/json',
        'x-amz-date': date2timestamp(date),
        'x-amz-content-sha256': hashedPayload
      },
      body: enc.encode(testFile)
    })
  ).catch((error) => {
    console.log(error)
  }).then((rsp) => {
    console.log('WE DID IT')
  })
}

async function canonicalRequest (httpMethod, canonicalUri, canonicalQueryString, canonicalHeaders, signedHeaders, hashedPayload) {
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

/* return 'PUT\n' +
    key + '\n' +
    '\n' +
    'content-md5:' + md5 + '\n' +
    'content-type:' + contentType + '\n' +
    'host:' + bucket + '\n' +
    'x-amz-object-lock-mode:GOVERNANCE' + '\n' +
    'x-amz-object-lock-retain-until-date:' + date2datestamp(lockUntil) + '\n' +
    '\n' +
    'content-md5;content-type;host;x-amz-object-lock-mode;x-amz-object-lock-retain-until-date' + '\n' +
    buf2hex(await crypto.subtle.digest('SHA-256', body)) */
