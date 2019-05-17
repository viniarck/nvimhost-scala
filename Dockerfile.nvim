FROM hseeberger/scala-sbt:11.0.2_2.12.8_1.2.8
LABEL maintainer "viniarck@gmail.com"

RUN \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y build-essential \
  git \
  python3 \
  python3-pip

RUN cd /tmp && curl -LO https://github.com/neovim/neovim/releases/download/v0.3.3/nvim-linux64.tar.gz && tar xzf nvim-linux64.tar.gz && cp /tmp/nvim-linux64/bin/nvim /usr/local/bin && cd /tmp/nvim-linux64/ && mkdir /share && cp -r share/* /share

ENV PATH=$PATH:/root/scala-2.12.8/bin
