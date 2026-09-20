FROM docker.io/messense/cargo-zigbuild@sha256:76ed3823d8cd9d8b409b10f9c4cda292b0c8699175ea4c0a2d541775c8184d2b

ARG RUST_VERSION
# bunsen needs newer rust
RUN rustup install ${RUST_VERSION}
RUN rustup default ${RUST_VERSION}
RUN rustup target add x86_64-apple-darwin
RUN rustup target add aarch64-apple-darwin
RUN rustup target add x86_64-unknown-linux-gnu
RUN rustup target add aarch64-unknown-linux-gnu
RUN rustup target add x86_64-unknown-freebsd

RUN dpkg --add-architecture arm64 && \
    apt-get update
RUN apt-get install -y \
    pkg-config \
    libasound2-dev
RUN apt-get install -y \
    libasound2-dev:arm64
RUN rm -rf /var/lib/apt/lists/*


ENV PKG_CONFIG_ALLOW_CROSS=1
ENV PKG_CONFIG_SYSROOT_DIR=/

# cpal needs newer sdk
RUN rm -rf /opt/MacOSX11.3.sdk
RUN /bin/sh -c 'curl -L "https://github.com/alexey-lysiuk/macos-sdk/releases/download/15.5/MacOSX15.5.tar.xz" | tar -J -x -C /opt'
ENV SDKROOT=/opt/MacOSX15.5.sdk

WORKDIR /