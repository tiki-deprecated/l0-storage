/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as b64 from './b64.js'

describe('b64.js Tests', function () {
  test('Encode Success', async () => {
    const utf8 = new TextEncoder()
    const res = b64.encode(utf8.encode('hello world'), false, true)
    expect(res).toBe('aGVsbG8gd29ybGQ=')
  })

  test('EncodeUrl Success', async () => {
    const raw = Uint8Array.of(132, 248, 185, 249, 128, 176, 69, 33)
    const res = b64.encode(raw, true, true)
    expect(res).toBe('hPi5-YCwRSE=')
  })

  test('EncodeNoPadding Success', async () => {
    const utf8 = new TextEncoder()
    const res = b64.encode(utf8.encode('hello world'), false, false)
    expect(res).toBe('aGVsbG8gd29ybGQ')
  })

  test('EncodeUrlNoPadding Success', async () => {
    const raw = Uint8Array.of(132, 248, 185, 249, 128, 176, 69, 33)
    const res = b64.encode(raw, true, false)
    expect(res).toBe('hPi5-YCwRSE')
  })

  test('Decode Success', async () => {
    const utf8 = new TextDecoder()
    const res = b64.decode('aGVsbG8gd29ybGQ=', false)
    expect(utf8.decode(res)).toBe('hello world')
  })

  test('DecodeUrl Success', async () => {
    const raw = Uint8Array.of(132, 248, 185, 249, 128, 176, 69, 33)
    const res = b64.decode('hPi5-YCwRSE=', true)
    expect(new Uint8Array(res)).toStrictEqual(raw)
  })

  test('DecodeNoPadding Success', async () => {
    const utf8 = new TextDecoder()
    const res = b64.decode('aGVsbG8gd29ybGQ', false)
    expect(utf8.decode(res)).toBe('hello world')
  })

  test('DecodeUrlNoPadding Success', async () => {
    const raw = Uint8Array.of(132, 248, 185, 249, 128, 176, 69, 33)
    const res = b64.decode('hPi5-YCwRSE', true)
    expect(new Uint8Array(res)).toStrictEqual(raw)
  })
})
