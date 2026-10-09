SUMMARY = "IM356-M-A serial-console hardware bring-up image"
LICENSE = "MIT"

inherit core-image extrausers

# printf "%q" $(mkpasswd -m sha256crypt fahel)
PASSWD = "\$5\$Hv7.3840I6SB21jN\$.xw0MzN1SbQFaKMhewg4GFMDetCHg4wBuuBxKkxNUa5"
EXTRA_USERS_PARAMS = "\
    usermod -p '${PASSWD}' root; \
    "

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
    firmwared \
"

IMAGE_INSTALL += " \
    can-utils \
    coreutils \
    cpufrequtils \
    dosfstools \
    ethtool \
    evtest \
    e2fsprogs-mke2fs \
    e2fsprogs-resize2fs \
    fbset \
    fsl-rc-local \
    iproute2 iproute2-tc \
    iw \
    i2c-tools \
    kernel-tools-iio \
    kernel-tools-pci \
    kernel-tools-virtio \
    kernel-tools-vsock \
    libgpiod-tools \
    linuxptp \
    memtester \
    minicom \
    mmc-utils \
    mtd-utils \
    mtd-utils-ubifs \
    nano \
    parted \
    procps \
    ptpd \
    udev-extraconf \
    chrony \
    git \
    cmake \
    vim \
    python3-core \
    python3-datetime \
    python3-json \
    python3-debugpy \
    python3-xmlrpclib-to \
    python3-modules \
    python3-misc \
    python3-paho-mqtt \
    python3-pygobject \
    python3-gpiod \
    python3-evdev \
    python3-numpy \
    python3-dbus \
    python3-sqlite3 \
    python3-netifaces \
    python3-posix-ipc \
    sox libmad mpg123 \
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
    i2c-tools \
    sqlite3 \
    net-snmp \
    journal-mount \
    baresip \
    baresip-apps \
    device-service \
"

IMAGE_INSTALL:append = " weston weston-init weston-examples"
SYSTEMD_DEFAULT_TARGET = "graphical.target"
