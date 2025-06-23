
SUMMARY = "Libre - A portable and generic library for real-time communications"
HOMEPAGE = "https://github.com/baresip/re"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/re.git;protocol=https;branch=main"
# v3.23.0 - 2025-06-04
SRCREV = "715d11751d8021c89c7bd0944b29820d3f6afb3e"

S = "${WORKDIR}/git"

DEPENDS = "openssl alsa-lib zlib pipewire"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
