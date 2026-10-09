FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:imx6u-lcd = " file://0001-media-sandbox-only-require-dri-for-hardware-codecs.patch"

FULL_OPTIMIZATION:remove:imx6u-lcd = "-g"