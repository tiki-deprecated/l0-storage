/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

const url = 'https://storage.l0.mytiki.com/api/latest/report'

export async function report (clientId, clientSecret, path, sizeBytes) {
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
