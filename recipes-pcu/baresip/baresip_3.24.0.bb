SUMMARY = "Baresip - A modular SIP User-Agent"
HOMEPAGE = "https://github.com/baresip/baresip"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/baresip.git;protocol=https;branch=main \
           file://com.github.Baresip.conf \
           file://baresip.service \
           file://config \
           file://accounts \
           file://my_phone_calling.wav \
           file://my_call_receiver.wav \
           file://my_hang_up.wav"

# v3.24.0 - 2025-07-09
SRCREV = "42286be6b221398c0523165352a37a5b26ed0af0"

S = "${WORKDIR}/git"

DEPENDS = "re openssl alsa-lib pipewire libopus glib-2.0 glib-2.0-native python3-packaging-native"
RDEPENDS:${PN} += "glib-2.0"

inherit cmake pkgconfig systemd python3native

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DCMAKE_PROGRAM_PATH=${STAGING_BINDIR_NATIVE}"

SYSTEMD_SERVICE:${PN} = "baresip.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${sysconfdir}/baresip
    install -m 0644 ${UNPACKDIR}/config ${D}${sysconfdir}/baresip
    install -m 0644 ${UNPACKDIR}/accounts ${D}${sysconfdir}/baresip

    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -m 0644 ${UNPACKDIR}/com.github.Baresip.conf ${D}${sysconfdir}/dbus-1/system.d/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/baresip.service ${D}${systemd_unitdir}/system

    install -d ${D}${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/my_phone_calling.wav ${D}${datadir}/${PN}/
    install -m 0644 ${UNPACKDIR}/my_call_receiver.wav ${D}${datadir}/${PN}/
    install -m 0644 ${UNPACKDIR}/my_hang_up.wav ${D}${datadir}/${PN}/
}

FILES:${PN} += "${sysconfdir}/baresip"
