FROM openjdk:9-b181-jre
MAINTAINER moxious@oldhat.org

RUN mkdir -p /theor
WORKDIR /theor

COPY init.sh /theor
COPY target/theor-0.0.2-jar-with-dependencies.jar /theor
ENV TZ=UTC

# Needed audio/video deps
RUN apt-get update && apt-get install -y --no-install-recommends \
   ffmpeg \
   id3v2 \
   timidity \
   lame \
   alsa-utils \
   alsa-oss \
   a2jmidid \
   fluidsynth \
   ams

RUN chmod +x /theor/init.sh

ENTRYPOINT ["/theor/init.sh"]