SUMMARY = "A image for PEI development"
LICENSE = "MIT"

inherit core-image extrausers

# printf "%q" $(mkpasswd -m sha256crypt fahel)
PASSWD = "\$5\$Hv7.3840I6SB21jN\$.xw0MzN1SbQFaKMhewg4GFMDetCHg4wBuuBxKkxNUa5"
EXTRA_USERS_PARAMS = "\
    usermod -p '${PASSWD}' root; \
    "

IMAGE_INSTALL:remove = "spidev-test"

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
    packagegroup-pei-tools \
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
    python3-netifaces \
    sox \
    curl \
    evtest \
    tzdata \
    v4l-utils \
    libv4l \
    libva \
    libgpiod \
    tslib \
    net-tools \
    tcpdump \
    libubootenv \
    libubootenv-bin \
    htop \
    baresip \
    pmond \
"
