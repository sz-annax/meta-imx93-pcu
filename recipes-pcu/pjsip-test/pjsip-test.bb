SUMMARY = "Example package with CMake and Git support"
DESCRIPTION = "An example application that uses CMake and Git"
LICENSE = "CLOSED"

# LINUX_IMX_SRC ?= "git://github.com/nxp-imx/linux-imx.git;protocol=https;branch=${SRCBRANCH}"
SRC_URI = "git://gitee.com/bigstep/pjsip-test.git;protocol=https;branch=master"
SRCREV = "c4b92f68ca797b89bd676fd46588fc51cd8cf280"

DEPENDS += "pjproject pkgconfig libgpiod"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

do_install() {
    install -d ${D}${bindir}
    install -m 0755 pjsua2_app ${D}${bindir}
}

FILES_${PN} = "${bindir}/pjsua2_app"
