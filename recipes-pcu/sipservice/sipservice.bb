SUMMARY = "Custom SIP Service"
DESCRIPTION = "A SIP service that uses libre and baresip libraries"
LICENSE = "CLOSED"

SRC_URI = "git://gitee.com/bigstep/sipservice.git;branch=master;protocol=https"
SRCREV = "${AUTOREV}"

DEPENDS = "re baresip"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
