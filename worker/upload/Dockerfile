FROM node:19

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -qy tini libc++1

WORKDIR /app

RUN npm install wrangler

ENV WRANGLER_SEND_METRICS=false

CMD ["npm", "run", "start"]
