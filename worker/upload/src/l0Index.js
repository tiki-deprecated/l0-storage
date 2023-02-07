/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import { SHA3 } from 'sha3'
import * as b64 from './b64.js'

export { report, decodeBlock, decodeCompactSize, decodeBigInt, txnList }

async function report (key, body, config) {
  const split = body.path.split('/')
  return fetch(config.url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(key.id + ':' + key.secret)
    },
    body: JSON.stringify({
      appId: split[0],
      address: split[1],
      block: split[2].replace(/\.block+$/, ''),
      src: config.bucket + '/' + body.path + '?versionId=' + body.version,
      transactions: txnList(body.block)
    })
  })
}

function txnList (block) {
  const list = []
  const decodedBlock = decodeBlock(new Uint8Array(block))
  const decodedBlockBody = decodeBlock(decodedBlock[1])
  const txnCount = decodeBigInt(decodedBlockBody[4])
  const hash = new SHA3(256)
  for (let i = 0; i < txnCount; i++) {
    hash.update(b64.encode(decodedBlockBody[5 + i]), 'base64')
    list.push(b64.encode(hash.digest(), true, false))
    hash.reset()
  }
  return list
}

function decodeBlock (bytes) {
  const extractedBytes = []
  let currentSize = 0

  for (let i = 0; i < bytes.length; i += currentSize) {
    currentSize = decodeCompactSize(bytes.subarray(i, bytes.length))

    const val = bytes[i] & 0xFF
    if (val <= 252) i++
    else if (val === 253) i += 3
    else if (val === 254) i += 5
    else i += 9

    const currentBytes = bytes.subarray(i, i + currentSize)
    extractedBytes.push(currentBytes)
  }
  return extractedBytes
}

function decodeCompactSize (compactSize) {
  const size = compactSize[0] & 0xFF

  let bytes = []
  if (size <= 252) return size
  else if (size === 253) bytes = compactSize.subarray(1, 3)
  else if (size === 254) bytes = compactSize.subarray(1, 5)
  else bytes = compactSize.subarray(1, 9)

  let value = 0
  for (let i = bytes.length - 1; i >= 0; i--) {
    value = value << 8
    value = value | (bytes[i] & 0xFF)
  }
  return value
}

function decodeBigInt (bytes) {
  const negative = bytes.length > 0 && ((bytes[0] & 0x80) === 0x80)
  let result
  if (bytes.length === 1) {
    result = BigInt(bytes[0])
  } else {
    result = 0n
    for (let i = 0; i < bytes.length; i++) {
      const item = BigInt(bytes[bytes.length - i - 1] & 0xFF)
      result |= item << (8n * BigInt(i))
    }
  }
  return result !== 0n
    ? negative
      ? BigInt.asIntN(8 * bytes.length, result)
      : result
    : BigInt(0)
}
