FROM node:19

ENV DEBIAN_FRONTEND=noninteractive

RUN apt -y update && 
    apt install -y tini libc++1

WORKDIR /app

CMD ["npm", "run", "start"]
