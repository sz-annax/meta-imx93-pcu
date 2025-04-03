SUMMARY = "Baresip - A modular SIP User-Agent"
HOMEPAGE = "https://github.com/baresip/baresip"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/baresip.git;branch=main;protocol=https"
SRCREV = "${AUTOREV}"

DEPENDS = "libre openssl alsa-lib pipewire"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
