/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

export { decode, encode }

function decode (string, isUrlEncoded) {
  const m = string.length % 4
  let input = string
  if (isUrlEncoded) {
    input = input
      .replace(/-/g, '+')
      .replace(/_/g, '/')
  }
  input.padEnd(string.length + (m === 0 ? 0 : 4 - m), '=')
  return Uint8Array.from(atob(input), c => c.charCodeAt(0)).buffer
}

function encode (bytes, urlEncode, withPad) {
  let res = btoa(String.fromCharCode.apply(null, bytes))
  if (urlEncode) {
    res = res
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
  }
  if (!withPad) {
    res = res.replace(/=+$/, '')
  }
  return res
}
