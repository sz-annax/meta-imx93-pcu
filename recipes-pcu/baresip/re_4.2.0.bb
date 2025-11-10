
SUMMARY = "Libre - A portable and generic library for real-time communications"
HOMEPAGE = "https://github.com/baresip/re"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/re.git;protocol=https;branch=main"
# v4.2.0 - 2025-11-03
SRCREV = "204fc48836906a2b675c97e1f651513b091e47c9"

S = "${WORKDIR}/git"

DEPENDS = "openssl alsa-lib zlib pipewire"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
