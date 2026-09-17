
DESCRIPTION = "Packagegroup to provide necessary tools for pei image"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PIPEWIRE_TOOLS = " \
    pipewire \
    pipewire-pulse \
    pipewire-spa-tools \
    pipewire-tools \
    ${PIPEWIRE_V4L2} \
"
PIPEWIRE_V4L2 = "pipewire-v4l2"
PIPEWIRE_V4L2:arm:imx-nxp-bsp = ""

RDEPENDS:${PN} = " \
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
    ${PIPEWIRE_TOOLS} \
    procps \
    ptpd \
    python3-core \
    python3-datetime \
    python3-json \
    python3-debugpy \
    python3-xmlrpclib-to \
    python3-modules \
    python3-misc \
    udev-extraconf \
"
