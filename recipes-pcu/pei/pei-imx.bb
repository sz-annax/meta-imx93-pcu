SUMMARY = "Download and install the pei-imx package with systemd service."
DESCRIPTION = "An example application that uses CMake and Git"
LICENSE = "CLOSED"

SRC_URI = "git://gitee.com/bigstep/pei-imx93.git;protocol=https;branch=master"
SRCREV = "d0c463024177e37215f2f541c94457b1df458c05"

DEPENDS += "pjproject pkgconfig libgpiod qtbase qtdeclarative qtmultimedia qtnetworkauth"

S = "${WORKDIR}/git"

inherit qt6-cmake pkgconfig
