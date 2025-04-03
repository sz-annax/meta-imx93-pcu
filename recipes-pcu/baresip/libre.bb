
SUMMARY = "Libre - A portable and generic library for real-time communications"
HOMEPAGE = "https://github.com/baresip/re"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/re.git;branch=main;protocol=https"
SRCREV = "${AUTOREV}"

DEPENDS = "openssl alsa-lib zlib pipewire"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
