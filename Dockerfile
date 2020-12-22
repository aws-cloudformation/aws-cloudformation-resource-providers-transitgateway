FROM python:alpine
MAINTAINER AWS
RUN apk add --update --no-cache \
      build-base \
      git \
      openssh-client \
      bash \
      curl

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
ARG APP_NAME
ENV GITHUB_USERNAME=$GITHUB_USERNAME

ENV APP_NAME=$APP_NAME

COPY . /app
WORKDIR /app
RUN git config --global user.email "$GITHUB_USERNAME@users.no-reply.github.com"
RUN git config --global user.name "$GITHUB_USERNAME"

RUN pip3 install pre-commit
CMD /bin/bash
