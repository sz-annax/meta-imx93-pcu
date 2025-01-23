SUMMARY = "A image for PEI development"

IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_LINGUAS = " "

LICENSE = "MIT"

inherit core-image populate_sdk_qt6

CONFLICT_DISTRO_FEATURES = "directfb"

## Select Image Features
IMAGE_FEATURES += " \
    debug-tweaks \
    tools-profile \
    tools-sdk \
    package-management \
    splash \
    nfs-client \
    tools-debug \
    ssh-server-openssh \
    tools-testapps \
    hwcodecs \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'weston', \
       bb.utils.contains('DISTRO_FEATURES',     'x11', 'x11-base x11-sato', \
                                                       '', d), d)} \
"

CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-core-full-cmdline \
    packagegroup-fsl-tools-audio \
    packagegroup-fsl-gstreamer1.0 \
    packagegroup-fsl-gstreamer1.0-full \
    firmwared \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'weston-xwayland xterm', '', d)} \
"

IMAGE_INSTALL += " \
    curl \
    packagegroup-qt6-imx \
    tzdata \
    qtmultimedia \
    qtmultimedia-plugins \
    qtmultimedia-qmlplugins \
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
    pjproject \
    pei-imx \
    htop \
"
