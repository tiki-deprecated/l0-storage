# Layer 0 Storage Service
### [📚 Docs](https://docs.mytiki.com) &nbsp;&nbsp;[💬 Discord](https://discord.gg/tiki)

Long-term (10+ years) immutable (WORM) low-frequency backup via a shared, cloud-hosted bucket. Used by the TIKI 
blockchain ([tiki-sdk-dart](https://github.com/tiki/tiki-sdk-dart)) to back up and restore blocks in the event of 
and edge-device failure.

For new projects, we recommend signing up at [console.mytiki.com](https://console.mytiki.com) and utilizing one of our
platform-specific SDKs which handle any required L0 Storage API calls.

- **🤖 Android: [tiki-sdk-android](https://github.com/tiki/tiki-sdk-android)**
- **🍎 iOS: [tiki-sdk-ios](https://github.com/tiki/tiki-sdk-ios)**
- **🦋 Flutter: [tiki-sdk-flutter](https://github.com/tiki/tiki-sdk-flutter)**

### [🎬 API Reference ➝](https://docs.mytiki.com/reference/l0-storage-upload-post)

### Basic Architecture
Objects are stored in s3 compatible ([Wasabi](https://wasabi.com)) bucket using Compliance-mode 
[Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock-overview.html) for immutable storage.
Object lock set to 10 years.

Upload requires a special access token in the format of JWT. Write access is scoped by the `sub` claim provisioned to 
keys under `base64Url(sha3_256(user id))/base64Url(sha3_256(public key))/*`. To request an access key requires proof
of private key pair. Access token have an expiration of 1 hour. Read access is public. Encrypt your data before
uploading if you want it kept private. 

#### Service

A [Spring Boot](https://github.com/spring-projects/spring-boot) microservice
using [Spring Security](https://github.com/spring-projects/spring-security) for token issuance, signing, and 
verification. Service handles API Id generation, revocation, and storage (required for uploads). API Ids are not private
keys and intended to live at the application layer (web/mobile). Usage is logged service level through a reporting API
to avoid expensive bucket crawling/scanning. 

Code follows TIKI's [vertical slice](https://jimmybogard.com/vertical-slice-architecture/) architecture and
nomenclature. For example, business logic for token issuance can be found in `TokenService.java`

#### Database
[PostgresSQL](https://www.postgresql.org) is used for persistence of API Ids, temporary token storage, and usage 
logging. See `/database` at the project root for database configuration scripts.

#### Infrastructure
As a microservice we utilize a 1 service - 1 database pattern, without state management. Services are containerized
using [Docker](https://www.docker.com) images to scale horizontally based on demand. Images are deployed simply behind
an application load balancer (no k8 needed) to
[Digital Ocean's App Platform](https://docs.digitalocean.com/products/app-platform/). The load balancer sits behind
Cloudflare [Proxied DNS](https://developers.cloudflare.com/fundamentals/get-started/concepts/how-cloudflare-works/) for
basic protection. 

Uploads are verified through a [Cloudflare Worker](https://workers.cloudflare.com) executing at the CDN layer without 
cold-start. The upload worker performs signature and payload verification, usage reporting, and ensures upload to 
provisioned destination.

Configuration TF scripts are located in the project root under `/infra`. Deployment
driven by GitHub Actions (see `.github/workflows/`) with [Terraform Cloud](https://www.terraform.io).