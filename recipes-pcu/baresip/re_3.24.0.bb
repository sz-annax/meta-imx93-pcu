
SUMMARY = "Libre - A portable and generic library for real-time communications"
HOMEPAGE = "https://github.com/baresip/re"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/re.git;protocol=https;branch=main"
# v3.24.0 - 2025-07-09
SRCREV = "16bb5c7662029def4b62b4ba686fd5d0c029086c"

S = "${WORKDIR}/git"

DEPENDS = "openssl alsa-lib zlib pipewire"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
