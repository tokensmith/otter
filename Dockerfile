FROM ubuntu:14.04

ARG MAVEN_VERSION=3.5.2
ARG GRADLE_VERSION=4.2
ARG GRADLE_HOME="/opt/gradle"
ARG USER_HOME_DIR="/root"

RUN \
  apt-get update && \
  apt-get install -y software-properties-common  && \
  apt-get update && \
  apt-get install -y python-software-properties && \
  apt-get update && \
  apt-get install -y wget && \
  apt-get update && \
  apt-get install unzip && \
  apt-get update && \
  apt-get -y install git && \
  apt-get update && \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  apt-get update && \
  rm -rf /var/lib/apt/lists/* && \
  mkdir -p /usr/share/maven /usr/share/maven/ref && \
  wget https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -O /tmp/apache-maven.tar.gz && \
  tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 && \
  rm -f /tmp/apache-maven.tar.gz && \
  ln -s /usr/share/maven/bin/mvn /usr/bin/mvn && \
  wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O /tmp/gradle.zip && \
  unzip /tmp/gradle.zip && \
  rm /tmp/gradle.zip && \
  mkdir -p ${GRADLE_HOME} && \
  mv gradle-${GRADLE_VERSION} ${GRADLE_HOME} && \
  ln -s ${GRADLE_HOME}/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle && \
  mkdir -p ${USER_HOME_DIR}/gradle/.gradle && \
  git clone https://github.com/RootServices/otter.git /data/otter

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
ENV GRADLE_HOME ${GRADLE_HOME}

VOLUME "$USER_HOME_DIR/gradle/.gradle"
VOLUME "$USER_HOME_DIR/.m2"

# Define working directory.
WORKDIR /data


# Define default command.
CMD ["bash"]
