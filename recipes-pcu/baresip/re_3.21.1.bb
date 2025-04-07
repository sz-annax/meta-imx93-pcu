
SUMMARY = "Libre - A portable and generic library for real-time communications"
HOMEPAGE = "https://github.com/baresip/re"
LICENSE = "CLOSED"

SRC_URI = "https://github.com/baresip/re/archive/refs/tags/v${PV}.tar.gz"
SRC_URI[sha256sum] = "2c55baed02f2eec6d0a79ca0ea0892c5a9c91a91f510ec47a316fa16b97c4f41"

DEPENDS = "openssl alsa-lib zlib pipewire"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
