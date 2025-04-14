SUMMARY = "A image for PEI development"
LICENSE = "MIT"
inherit core-image

IMAGE_FEATURES += " \
    debug-tweaks \
    tools-profile \
    tools-sdk \
    package-management \
    ssh-server-openssh \
    nfs-client \
"

CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-core-full-cmdline \
    packagegroup-fsl-tools-audio \
    packagegroup-imx-core-tools \
    firmwared \
"

IMAGE_INSTALL += " \
    chrony \
    coreutils \
    git \
    cmake \
    vim \
    python3-paho-mqtt \
    python3-pygobject \
    python3-gpiod \
    python3-evdev \
    python3-numpy \
    python3-dbus \
    sox \
    curl \
    evtest \
    tzdata \
    v4l-utils \
    libv4l \
    ffmpeg \
    libva \
    libgpiod \
    tslib \
    net-tools \
    tcpdump \
    libubootenv \
    libubootenv-bin \
    htop \
    baresip \
"
