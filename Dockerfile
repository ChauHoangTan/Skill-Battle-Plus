FROM jenkins/jenkins:lts

# Switch to root to install packages
USER root

# Cài git, docker CLI hoặc các tool cần thiết
RUN apt-get update && apt-get install -y git docker.io && rm -rf /var/lib/apt/lists/*

# Set quyền cho thư mục Jenkins
RUN chown -R jenkins:jenkins /var/jenkins_home

# Set git config global cho user jenkins
USER jenkins
RUN git config --global user.name "Jenkins CI" \
    && git config --global user.email "jenkins@example.com"

# Quay lại user jenkins mặc định
USER jenkins