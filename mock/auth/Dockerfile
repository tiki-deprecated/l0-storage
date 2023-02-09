# TODO(@timoguin): This section can be used to automatically generate
# a set of JWKs instead of us just copying in a static one.
#
# # Build image for compiling app
# FROM golang:1.19.5-alpine3.17 AS build
# 
# WORKDIR /app
# 
# # Install Go deps and build app
# COPY go.mod go.sum ./
# RUN go mod download 
# COPY main.go ./
# RUN go build -o /jwk-keygen
# 
# # Final image with single binary
# FROM scratch
# COPY --from=build /jwk-keygen /jwk-keygen
# ENTRYPOINT ["/jwk-keygen"]

# ======================================================

# Need a multi-stage build because the static-web-server image is based on scratch
# and doesn't have a shell to be able to run the "mkdir" command.
FROM alpine:3.17.1 AS build

RUN mkdir -p /public/.well-known
COPY jwks.json /public/.well-known/jwks.json

# ======================================================

# Web server for static JWKs file
FROM joseluisq/static-web-server:2.14.1

COPY --from=build /public/.well-known/jwks.json /public/.well-known/jwks.json
