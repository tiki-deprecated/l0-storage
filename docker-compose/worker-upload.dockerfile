FROM node:19

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -qy tini libc++1

WORKDIR /app
COPY ../worker/upload ./

RUN npm install -g wrangler
RUN npm ci

ENV WRANGLER_SEND_METRICS=false

CMD ["wrangler", "dev", "--local"]
