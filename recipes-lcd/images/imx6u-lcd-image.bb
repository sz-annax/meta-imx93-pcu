SUMMARY = "IM356-M-A serial-console hardware bring-up image"
LICENSE = "MIT"

require recipes-core/images/core-image-minimal.bb

COMPATIBLE_MACHINE = "^imx6u-lcd$"
IMAGE_INSTALL:append = " kernel-modules memtester mmc-utils ethtool iproute2"
IMAGE_ROOTFS_EXTRA_SPACE = "65536"
