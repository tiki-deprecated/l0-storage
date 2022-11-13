/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as wasabi from './wasabi.js'

// from https://docs.aws.amazon.com/AmazonS3/latest/API/sig-v4-header-based-auth.html
const testKey = 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY'
const testId = 'AKIAIOSFODNN7EXAMPLE'
const testDate = new Date('2013-05-24T00:00:00Z')
const testRegion = 'us-east-1'
const testService = 's3'
const testCanonicalRequest = 'GET\n' +
    '/test.txt\n' +
    '\n' +
    'host:examplebucket.s3.amazonaws.com\n' +
    'range:bytes=0-9\n' +
    'x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n' +
    'x-amz-date:20130524T000000Z\n' +
    '\n' +
    'host;range;x-amz-content-sha256;x-amz-date\n' +
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855'
const testStringToSign = 'AWS4-HMAC-SHA256\n' +
    '20130524T000000Z\n' +
    '20130524/us-east-1/s3/aws4_request\n' +
    '7344ae5b7ee6c3e7e6b0fe0640412a37625d1fbfff95c48bbb2dc43964946972'
const testSignature = 'f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41'
const testAuthorization = 'AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/us-east-1/s3/aws4_request,SignedHeaders=host;range;x-amz-content-sha256;x-amz-date,Signature=f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41'

test('CanonicalRequest Success', async () => {
  const hashedPayload = wasabi.buf2hex(
    await crypto.subtle.digest('SHA-256', new TextEncoder().encode('')))

  const req = wasabi.canonicalRequest(
    'GET',
    '/test.txt',
    undefined,
    'host:examplebucket.s3.amazonaws.com' + '\n' +
      'range:bytes=0-9' + '\n' +
      'x-amz-content-sha256:' + hashedPayload + '\n' +
      'x-amz-date:' + wasabi.date2timestamp(testDate) + '\n',
    'host;range;x-amz-content-sha256;x-amz-date',
    hashedPayload
  )

  expect(req).toBe(testCanonicalRequest)
})

test('StringToSign Success', async () => {
  const s2s = await wasabi.stringToSign(testDate, testRegion, testService, testCanonicalRequest)
  expect(s2s).toBe(testStringToSign)
})

test('Signature Success', async () => {
  const signKey = await wasabi.signingKey(testKey, testDate, testRegion, testService)
  const signature = wasabi.buf2hex(await wasabi.hmacSha(new TextEncoder().encode(testStringToSign), signKey))
  expect(signature).toBe(testSignature)
})

test('Authorization Success', async () => {
  const signedHeaders = 'host;range;x-amz-content-sha256;x-amz-date'
  const authorization = wasabi.authorization(testId, testDate, testRegion, testService, signedHeaders, testSignature)
  expect(authorization).toBe(testAuthorization)
})
