SUMMARY = "Baresip - A modular SIP User-Agent"
HOMEPAGE = "https://github.com/baresip/baresip"
LICENSE = "CLOSED"

SRC_URI = "https://github.com/baresip/baresip/archive/refs/tags/v${PV}.tar.gz"
SRC_URI[sha256sum] = "cdd4ee5b37e3a21b12848f1e14b7998cdb23c040e2057909b3e6725ba1799322"

DEPENDS = "re openssl alsa-lib pipewire libopus"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
