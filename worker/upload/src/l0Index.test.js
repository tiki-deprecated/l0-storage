/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

import * as l0Index from './l0Index.js'
import * as b64 from './b64.js'

test('NotBlock Success', async () => {
  const key = 'c45XTIRZ2078mDgY-GNJw8M9AK6KTdWuZ7naXX0zpys/bwB3Y3VbQBuor0b0WDq8k5Cvj7HMXa-QNyvyNyev7C0/public.key'
  const content = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiwwU+1cP/kwB0Usd3N/MhOJlD8SKtDc5nmgq6QOml7Hfet55R7BqzXrOYOn8bRX6astUSRmO1orfnmAPkCj2xYgPgG2hOIm6Uhr1VH08GtrhXtwkAndXUFMs0Lb5WlmwHzhG0PxTYlAHyiLakGa6vSgZ4hDMS04r9lNaX9gqe4JvzSqCb698wL6Bz+hu3RkbTabtnFzb+i7uemp3LaX/2fdUtvZIn8WkfVv7gLgXavhotiagD46HNVMzg8i1d5JMmFV0gnxsC4wOw/DCH6wsJS24bPcQN3AtqUknJKcXwUwckT44OvGkdBc8JhOXVg3q8K9eMGfbvpNpNJJi8lNozQIDAQAB'

  const contentBytes = Uint8Array.from(Buffer.from(content, 'base64'))
  await l0Index.report('', '', key, contentBytes)
  // expect(claims.iss).toBe(testIss)
})

test('Block Success', async () => {
  const key = 'c45XTIRZ2078mDgY-GNJw8M9AK6KTdWuZ7naXX0zpys/0JW5xhAM_I0kyCkOrpHBpJ8T-9zKU-ziRdNhhi9eKCY/rEElLgV7rWXf-KDqCLZvF19SjQJ9Xr5P1paSGRRtocE.block'
  const content = '/QABL/b00Rh3Pn9ADTlqLTOlSqgtJBSUk27Ath8VepihUYAKUAJjL/qwfK6MsGPInGBYvXnbkqD/5Es9PNqnDZeJtHsNsUA3bhzsqMH6X8XfPWU71ZGbrkwSilM3094x1e6gVDbL7uTAHO3THTiEq5uY0qCDyd+G8tky3N3UwQq3lgc56iUNXda84y+MDVlxspHPvO1CBFPJjsfM7gU5VwD9oUGtZhxR6ZtBO21JBhZEOIHSzN+3rxgOmVqYFZQPRoRdXiNoRjMjZgG3fep5cIgrK6eSX5Z2oOdyb033PiLmBKjLio83x6noJAu69kVDl88KcP5JijdB/E29KSBkK3uoVP2wAQEBBGPO5gEgqbplkmHccgNwpcXqZmAOFAE5CLYbV6IeKgGLA+NVp78gpTG3DsZR2xBXr76Ck3XxDQS7c+x7rKC4+EgoZDlNr4kBAf1iAQEBINCVucYQDPyNJMgpDq6RwaSfE/vcylPs4kXTYYYvXigmBGPO5dwBAP0AAUtJLa6HTj4QjYDkCxkLhPMtBW8Zp5dUchYIlL7Hekb6j/nghCuW7+0at7NALhHR8CzORA430BSN4SLcr6AWENS2Ap4Oy8TkDuBnk4F5mToM5CWp/pnGxJNvEhhmCGFSUa4oYMH3+Ho8rWvdWioGEz1I6UDzVl0qhQO+1rWr75zKyKP6hgiq12XUI6rylHRQwjWmNMRSaXN+gEoURVwKzpgDm5Wxo4U8/p+YNbRs6SkJ96xovzuqrAieWRnDns05XxGIiz6j3R/s51MRmEqNIiIcoFoXBR9CG64YwXz75DXsa87ttSatqJW78o+9T7pkNmTnEKy5hPi5+YCwRLfZtZw0INcbgh8plh39dHSTqiW+0QE8Y10JrOFFCnvRAuSFB1gJDAVbIioiXQVbIioiXQEAAQABAA=='
  const contentBytes = b64.decode(content)
  await l0Index.report('', {
    path: key,
    block: contentBytes
  }, {
    url: 'http://localhost:10504/api/latest/report',
    bucket: 'https://bucket.storage.l0.mytiki.com'
  })
  // expect(claims.iss).toBe(testIss)
})
