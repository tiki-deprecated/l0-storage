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

FROM joseluisq/static-web-server:2.14.1

RUN mkdir /public/.well-known
COPY jwks.json /public/.well-known/jwks.json
