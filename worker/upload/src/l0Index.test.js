/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as l0Index from './l0Index.js'
import * as b64 from './b64.js'

test('TxnList Success', async () => {
  const content = '/QABL/b00Rh3Pn9ADTlqLTOlSqgtJBSUk27Ath8VepihUYAKUAJjL/qwfK6MsGPInGBYvXnbkqD/5Es9PNqnDZeJtHsNsUA3bhzsqMH6X8XfPWU71ZGbrkwSilM3094x1e6gVDbL7uTAHO3THTiEq5uY0qCDyd+G8tky3N3UwQq3lgc56iUNXda84y+MDVlxspHPvO1CBFPJjsfM7gU5VwD9oUGtZhxR6ZtBO21JBhZEOIHSzN+3rxgOmVqYFZQPRoRdXiNoRjMjZgG3fep5cIgrK6eSX5Z2oOdyb033PiLmBKjLio83x6noJAu69kVDl88KcP5JijdB/E29KSBkK3uoVP2wAQEBBGPO5gEgqbplkmHccgNwpcXqZmAOFAE5CLYbV6IeKgGLA+NVp78gpTG3DsZR2xBXr76Ck3XxDQS7c+x7rKC4+EgoZDlNr4kBAf1iAQEBINCVucYQDPyNJMgpDq6RwaSfE/vcylPs4kXTYYYvXigmBGPO5dwBAP0AAUtJLa6HTj4QjYDkCxkLhPMtBW8Zp5dUchYIlL7Hekb6j/nghCuW7+0at7NALhHR8CzORA430BSN4SLcr6AWENS2Ap4Oy8TkDuBnk4F5mToM5CWp/pnGxJNvEhhmCGFSUa4oYMH3+Ho8rWvdWioGEz1I6UDzVl0qhQO+1rWr75zKyKP6hgiq12XUI6rylHRQwjWmNMRSaXN+gEoURVwKzpgDm5Wxo4U8/p+YNbRs6SkJ96xovzuqrAieWRnDns05XxGIiz6j3R/s51MRmEqNIiIcoFoXBR9CG64YwXz75DXsa87ttSatqJW78o+9T7pkNmTnEKy5hPi5+YCwRLfZtZw0INcbgh8plh39dHSTqiW+0QE8Y10JrOFFCnvRAuSFB1gJDAVbIioiXQVbIioiXQEAAQABAA=='
  const contentBytes = b64.decode(content)
  const txnList = l0Index.txnList(contentBytes)

  expect(txnList.length).toBe(1)
  expect(txnList[0]).toBe('aUoaabtrKe8bA2oQL3f_PwhWG2rFJprZ-D3PHc2O-Gk')
})

test('DecodeBlock Success', async () => {
  const b64Block = '/QABL/b00Rh3Pn9ADTlqLTOlSqgtJBSUk27Ath8VepihUYAKUAJjL/qwfK6MsGPInGBYvXnbkqD/5Es9PNqnDZeJtHsNsUA3bhzsqMH6X8XfPWU71ZGbrkwSilM3094x1e6gVDbL7uTAHO3THTiEq5uY0qCDyd+G8tky3N3UwQq3lgc56iUNXda84y+MDVlxspHPvO1CBFPJjsfM7gU5VwD9oUGtZhxR6ZtBO21JBhZEOIHSzN+3rxgOmVqYFZQPRoRdXiNoRjMjZgG3fep5cIgrK6eSX5Z2oOdyb033PiLmBKjLio83x6noJAu69kVDl88KcP5JijdB/E29KSBkK3uoVP2wAQEBBGPO5gEgqbplkmHccgNwpcXqZmAOFAE5CLYbV6IeKgGLA+NVp78gpTG3DsZR2xBXr76Ck3XxDQS7c+x7rKC4+EgoZDlNr4kBAf1iAQEBINCVucYQDPyNJMgpDq6RwaSfE/vcylPs4kXTYYYvXigmBGPO5dwBAP0AAUtJLa6HTj4QjYDkCxkLhPMtBW8Zp5dUchYIlL7Hekb6j/nghCuW7+0at7NALhHR8CzORA430BSN4SLcr6AWENS2Ap4Oy8TkDuBnk4F5mToM5CWp/pnGxJNvEhhmCGFSUa4oYMH3+Ho8rWvdWioGEz1I6UDzVl0qhQO+1rWr75zKyKP6hgiq12XUI6rylHRQwjWmNMRSaXN+gEoURVwKzpgDm5Wxo4U8/p+YNbRs6SkJ96xovzuqrAieWRnDns05XxGIiz6j3R/s51MRmEqNIiIcoFoXBR9CG64YwXz75DXsa87ttSatqJW78o+9T7pkNmTnEKy5hPi5+YCwRLfZtZw0INcbgh8plh39dHSTqiW+0QE8Y10JrOFFCnvRAuSFB1gJDAVbIioiXQVbIioiXQEAAQABAA=='
  const blockBytes = b64.decode(b64Block)
  const decoded = l0Index.decodeBlock(new Uint8Array(blockBytes))

  expect(decoded.length).toBe(2)
  expect(decoded[0].length).toBe(256)

  const decodedBody = l0Index.decodeBlock(decoded[1])
  expect(decodedBody.length).toBe(6)
  expect(decodedBody[0][0]).toBe(1)
})

test('DecodeBigInt Success', async () => {
  const enc1 = b64.decode('xow7', true)
  const enc2 = b64.decode('Aw==', true)
  const enc3 = b64.decode('_Q==', true)
  const enc4 = b64.decode('Bys1dpH3Ozk=', true)

  const bg1 = l0Index.decodeBigInt(new Uint8Array(enc1))
  const bg2 = l0Index.decodeBigInt(new Uint8Array(enc2))
  const bg3 = l0Index.decodeBigInt(new Uint8Array(enc3))
  const bg4 = l0Index.decodeBigInt(new Uint8Array(enc4))

  expect(bg1).toBe(-3765189n)
  expect(bg2).toBe(3n)
  expect(bg3).toBe(-3n)
  expect(bg4).toBe(516565365635365689n)
})

test('DecodeCompactSize Success', async () => {
  const val1 = 'hello world'
  const val2 = 'hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world hello world'

  const enc1 = b64.decode('C2hlbGxvIHdvcmxk', true)
  const enc2 = b64.decode('_dMBaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQgaGVsbG8gd29ybGQ', true)

  const size1 = l0Index.decodeCompactSize(new Uint8Array(enc1))
  const size2 = l0Index.decodeCompactSize(new Uint8Array(enc2))
  const utf8 = new TextEncoder()

  expect(size1).toBe(utf8.encode(val1).length)
  expect(size2).toBe(utf8.encode(val2).length)
})

// add b64 test file
// test encode, encode url, encode no padding, encode no padding url
// tesd decode, decode url, decode no padding, decode no padding url
