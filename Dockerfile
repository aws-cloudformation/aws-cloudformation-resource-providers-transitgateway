FROM python:alpine
MAINTAINER AWS
RUN apk add --update --no-cache \
      build-base \
      linux-headers \
      tzdata \
      git \
      libxml2-dev \
      libxslt-dev \
      openssh-client \
      bash \
      curl

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
ARG APP_NAME
ENV GITHUB_USERNAME=$GITHUB_USERNAME

ENV APP_NAME=$APP_NAME

COPY . /repo
WORKDIR /repo

RUN pip3 install pre-commit
#RUN git config --global user.email '$GITHUB_USERNAME@users.noreply.github.com'
#RUN git config --global user.name $GITHUB_USERNAME
CMD /bin/bash -c pre-commit run --all-files
