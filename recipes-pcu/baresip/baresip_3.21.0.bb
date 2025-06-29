SUMMARY = "Baresip - A modular SIP User-Agent"
HOMEPAGE = "https://github.com/baresip/baresip"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/baresip.git;protocol=https;branch=main \
           file://baresip.service \
           file://baresip.conf"

# v3.23.0 - 2025-06-04
SRCREV = "b153ccd22049c07551b808bb0d0ce4bfb9ba27be"

S = "${WORKDIR}/git"

DEPENDS = "re openssl alsa-lib pipewire libopus glib-2.0 glib-2.0-native"
RDEPENDS:${PN} += "glib-2.0"

inherit cmake pkgconfig systemd

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DCMAKE_PROGRAM_PATH=${STAGING_BINDIR_NATIVE}"

SYSTEMD_SERVICE:${PN} = "baresip.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${sysconfdir}/baresip
    install -m 0644 ${UNPACKDIR}/baresip.conf ${D}${sysconfdir}/baresip

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/baresip.service ${D}${systemd_unitdir}/system
}

FILES_${PN} += "${sysconfdir}/baresip/baresip.conf"
